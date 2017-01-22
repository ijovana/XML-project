module.exports = [
	'$scope', '$http', 'amendmentService', 'lawsService', 'sessionService', '$state','$stateParams', '$window',
	function addSessionController($scope, $http, amendmentService, lawsService, sessionService, $state, $stateParams, $window){
			/*
			$scope.suggestedAmendmentsList = [];
			$scope.suggestedRegulations = [];
			$scope.suggestedRegulationsList=[];
			$scope.chosenAmendments = {
				amendments:[]
			};

			$scope.chosenRegulations= {
				regulations:[]
			}
			*/

			/*
			$scope.fillData = function()
			{
				$scope.currentSessionId = $stateParams.id;

				amendmentService.get_suggestedAmendments().then(function(response){
					var x2js = new X2JS();
					$scope.suggestedAmendments = x2js.xml2js(response);
					console.log("Predlozeni amandmani:  "+JSON.stringify($scope.suggestedAmendments));
					if($scope.suggestedAmendments.database.result.length==undefined)
					{
						var id = $scope.suggestedAmendments.database.result.id._ID;
	        			var naziv = $scope.suggestedAmendments.database.result.naziv._naziv;
	        			$scope.suggestedAmendmentsList.push({'naziv': naziv, 'ID': id});
					}else
					{
						for(var i=0; i<$scope.suggestedAmendments.database.result.length; i++)
						{
							var id = $scope.suggestedAmendments.database.result[i].id._ID;
	        				var naziv = $scope.suggestedAmendments.database.result[i].naziv._naziv;
	        				$scope.suggestedAmendmentsList.push({'naziv': naziv, 'ID': id});
						}
					}
				});


				lawsService.get_suggestedRegulations().then(function(response){
					var x2js = new X2JS();
					$scope.suggestedRegulations = x2js.xml2js(response);
					console.log("Predlozeni propisi:  "+JSON.stringify($scope.suggestedRegulations));

					if($scope.suggestedRegulations.database.result.length==undefined)
					{
						var id = $scope.suggestedRegulations.database.result.id._ID;
	        			var naziv = $scope.suggestedRegulations.database.result.naziv.naziv.__text;
	        			$scope.suggestedRegulationsList.push({'naziv': naziv, 'ID': id});
					}else
					{
						for(var i=0; i<$scope.suggestedRegulations.database.result.length; i++)
						{
							var id = $scope.suggestedRegulations.database.result[i].id._ID;
	        				var naziv = $scope.suggestedRegulations.database.result[i].naziv.naziv.__text;
	        				$scope.suggestedRegulationsList.push({'naziv': naziv, 'ID': id});
						}
					}
					

				});

			};

			$scope.fillData();
			*/
			$scope.edit_session = function()
			{
				console.log("Clicked!");
				console.log("Izabrani zakoni: "+$scope.chosenRegulations.regulations);
				console.log("Izabrani amandmani: "+$scope.chosenAmendments.amendments);
			};
	}
];