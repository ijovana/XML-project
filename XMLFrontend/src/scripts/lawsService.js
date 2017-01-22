module.exports = [
	'$http', '$window', '$q',
	function lawsService($http, $window, $q){


		function get_all_laws()
		{
			var resUrl = "http://localhost:8080/XML-project/regulations";
			return $http.get(resUrl)
			.then(function(response) {
				return response.data;
			});
			//return "Peeeh";
		}

		function create_law(JSONdata)
		{

			console.log("Sending data from service: "+JSONdata);

			return $http({
                    method: "post",
                    url: "http://localhost:8080/XML-project/regulations",
                    headers: { "Content-Type": 'application/xml' },
                    data: JSONdata
					
           	}).success(function(response){
				return response.data;				
			}).error(function(response, data){
				alert("Neuspešan unos predloga zakona.");
			});

		}

		function get_testRegulationPDF()
		{
			var resUrl = "http://localhost:8080/XML-project/regulationsPDF/n";
			return $http.get(resUrl)
			.then(function(response) {
				$window.open(resUrl);
			});
		}

		function get_testRegulationPDFDownload()
		{
			var resUrl = "http://localhost:8080/XML-project/regulationsPDF/1/pdf";
			return $http.get(resUrl)
			.then(function(response) {
				$window.open(resUrl);
			});
		}

		function get_suggestedRegulations()
		{
			var resUrl = "http://localhost:8080/XML-project/regulations/getSuggestedRegulations";
			return $http.get(resUrl)
			.then(function(response) {
				return response.data;
			});
		}

		return {
			get_all_laws: get_all_laws,
			create_law: create_law,
			get_testRegulationPDF: get_testRegulationPDF,
			get_testRegulationPDFDownload: get_testRegulationPDFDownload,
			get_suggestedRegulations: get_suggestedRegulations, 
		}

	}

];