module.exports = [
	'$http', '$window', '$q',
	function amendmentService($http, $window, $q){

		
		function get_sectionIds(id)
		{
			var resUrl = "http://localhost:8080/XML-project/regulations/getSectionIds/"+id;
			return $http.get(resUrl)
			.then(function(response) {
				return response.data;
			});
		}
			
		function get_articleIds(id)
		{
			var resUrl = "http://localhost:8080/XML-project/regulations/getArticleIds/"+id;
			return $http.get(resUrl)
			.then(function(response) {
				console.log("service data "+response.data);
				return response.data;
			});
		}

		function create_amendment(data)
		{
			console.log("Sending data from service: "+data);

			return $http({
                    method: "post",
                    url: "http://localhost:8080/XML-project/amendments",
                    headers: { "Content-Type": 'application/xml' },
                    data: data
					
           	}).success(function(response){
				return response.data;				
			}).error(function(response, data){
				alert("Neuspešan unos predloga amandmana.");
			});
		}

		function get_suggestedAmendments()
		{
			var resUrl = "http://localhost:8080/XML-project/amendments/getSuggestedAmendments";
			return $http.get(resUrl)
			.then(function(response) {
				return response.data;
			});

		}

		return {
			get_sectionIds: get_sectionIds,
			get_articleIds: get_articleIds,
			create_amendment: create_amendment,
			get_suggestedAmendments: get_suggestedAmendments,
		};


	}
];