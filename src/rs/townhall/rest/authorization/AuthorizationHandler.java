package rs.townhall.rest.authorization;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.message.Message;

import rs.townhall.entity.user.Korisnik;

@Provider
public class AuthorizationHandler implements RequestHandler {
	
	// Role-based access control
	private static HashMap<String, HashMap<String, ArrayList<String>>> rbac;

	static {
		rbac = new HashMap<String, HashMap<String, ArrayList<String>>>();

		Properties properties = new Properties();
		try {
			URL path = AuthorizationHandler.class.getClassLoader()
					.getResource("rs/townhall/rest/authorization/rbac.properties");
			File file = new File(path.getPath());
			FileInputStream fileInput = new FileInputStream(file);
			properties.load(fileInput);
			fileInput.close();

			Enumeration<Object> enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);

				String[] methods = value.split("#");
				HashMap<String, ArrayList<String>> methodData = new HashMap<String, ArrayList<String>>();
				for (int i = 1; i < methods.length; i++) {
					String[] data = methods[i].split(":");
					String[] urls = data[1].split("\\s");
					urls = Arrays.copyOfRange(urls, 1, urls.length);
					methodData.put(data[0].trim(), new ArrayList<String>(Arrays.asList(urls)));
				}

				rbac.put(key.trim(), methodData);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// debug
		printRBAC();
	}

	@Context
	private ServletContext context;

	private static final String LOGIN_URL = "/auth/login";
	private static final String HOME_URL = "/XML-project";

	@Override
	public Response handleRequest(Message arg0, ClassResourceInfo arg1) {
		HttpServletRequest request = (HttpServletRequest) arg0.get("HTTP.REQUEST");
		String url = request.getRequestURI().substring(request.getContextPath().length()); 
		String method = request.getMethod();
		
		// Skip authorization
		//if (false) {
		if (!url.equals(LOGIN_URL)) {
			HttpServletResponse response = (HttpServletResponse) arg0.get("HTTP.RESPONSE");

			String token = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (token == null)
				return Response.status(403).build();
			
			try {
				Korisnik user = validateToken(token);
				rbacCheck(user, method, url);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: Optimize.. don't allow to go throw method
				try {
					response.sendRedirect(HOME_URL);
				} catch (IOException e1) {
					System.out.println("Exception occured: " + e.getMessage());
				}
			}
		}
		return null;
	}

	private Korisnik validateToken(String token) throws Exception {
		Korisnik user = (Korisnik) this.context.getAttribute(token);
		if (user == null)
			throw new Exception("Invalid token");

		return user;
	}
	
	private void rbacCheck(Korisnik user, String method, String path) throws Exception{
		HashMap<String, ArrayList<String>> role = rbac.get(user.getUloga());
		ArrayList<String> urls = role.get(method);
		for (int i=0; i<urls.size(); i++) {
			String url = urls.get(i);
			if(!url.contains("/{id}")) {
				if(url.equals(path))
					return;
			} else if (url.contains("/{id}")) {
				String urlBase = url.substring(0, url.indexOf("/{id}"));
				if((path.startsWith(urlBase)) && (path.length() > urlBase.length()))
					return;
			}
		}
		
		throw new Exception("Role-based access control: Unauthorized access");
	}

	private static void printRBAC() {
		for (Map.Entry<String, HashMap<String, ArrayList<String>>> entry : rbac.entrySet()) {
		    System.out.println("Role: " + entry.getKey());
		    
		    HashMap<String, ArrayList<String>> value = entry.getValue();
		    for (Map.Entry<String, ArrayList<String>> _entry : value.entrySet()) {
		    	System.out.println("  Method: " + _entry.getKey());
		    	
		    	ArrayList<String> urls = _entry.getValue();
		    	for(int i=0; i<urls.size(); i++)
		    		System.out.println("    " + urls.get(i));
		    }
		}
	}
}