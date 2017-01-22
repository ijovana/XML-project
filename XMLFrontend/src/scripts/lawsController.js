module.exports = [
	'$scope', '$http','lawsService', '$routeParams','$window',
	function myController($scope, $http, lawsService, $routeParams, $window){

		console.log(' LAWS CONTROLLER! ');

    	function fillData(){
    		lawsService.get_all_laws()
				.then(function(response){
				$scope.getRegulations = response;
			});
		};

		fillData();

		$scope.test_pdf = function()
		{
			lawsService.get_testRegulationPDF().then(function(response){
			});
		};

		$scope.test_pdf_download = function()
		{
			lawsService.get_testRegulationPDFDownload().then(function(response){
				console.log("TEST DOWNLOAD...");
			});
		};
	}
];