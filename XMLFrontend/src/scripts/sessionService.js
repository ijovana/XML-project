module.exports = [
	'$http', '$window', '$q',
	function sessionService($http, $window, $q){

		function create_session(data)
		{
			
			console.log("Sending data from service: "+data);

			return $http({
                    method: "post",
                    url: "http://localhost:8080/XML-project/sessions",
                    headers: { "Content-Type": 'application/xml' },
                    data: data
					
           	}).then(function(response){
           		//console.log("Resonse data "+response.data);
				return response.data;				
			});
		}

		function get_session(id)
		{
			var resUrl = "http://localhost:8080/XML-project/sessions/"+id;
			return $http.get(resUrl)
			.then(function(response) {
				return response.data;
			});
		}

		function edit_session(id, data)
		{	
			var url = "http://localhost:8080/XML-project/sessions/"+id+"/";
			return $http({
                    method: "put",
                    url: url,
                    data: data
           	}).then(function(response){
				return response.data;				
			});
		}

		function edit_session_state(id, state)
		{
			var url = "http://localhost:8080/XML-project/sessions/"+id+"/"+state;
			return $http({
                    method: "put",
                    url: url
           	}).then(function(response){
				return response.data;				
			});
		}

		function set_timetable(id, data)
		{
			var url = "http://localhost:8080/XML-project/sessions/setTimetable/"+id;
			console.log("URL: "+url);
			console.log("DATA: "+data);
			return $http({
                    method: "put",
                    url: url,
                    headers: { "Content-Type": 'application/xml' },
                    data: data
           	}).then(function(response){
           		console.log("DONE.")
				return response.data;				
			});
		};

		function edit_session_results(id, data)
		{	
			var url = "http://localhost:8080/XML-project/sessions/";
			console.log("URL: "+url);
			console.log("DATA: "+data);
			return $http({
                    method: "put",
                    url: url,
                    headers: { "Content-Type": 'application/xml' },
                    data: data
           	}).then(function(response){
				return response.data;				
			});
		};


		return {
			create_session: create_session, 
			get_session: get_session,
			edit_session: edit_session, 
			edit_session_state: edit_session_state,
			set_timetable: set_timetable,
			edit_session_results: edit_session_results, 
		};

	}
];