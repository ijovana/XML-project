package rs.townhall.dao.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class AuthenticationInterceptor {

	@AroundInvoke
	public Object intercept(InvocationContext context) throws Exception {
		// TODO: add some logic here..
		
		System.out.println("SimpleInterceptor - Logging BEFORE calling method :" + context.getMethod().getName());

		Object result = context.proceed();
		
		System.out.println("SimpleInterceptor - Logging AFTER calling method :" + context.getMethod().getName());
		return result;
	}
}
