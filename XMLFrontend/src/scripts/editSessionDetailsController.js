	module.exports = [
	'$scope', '$http', 'amendmentService', 'lawsService', 'sessionService', '$state','$stateParams', '$window',
	function sessionController($scope, $http, amendmentService, lawsService, sessionService, $state, $stateParams, $window){

			$scope.listaStanja = [{'naziv': 'predlozi propis', 'vrednost':'predlozi_propis'}, 
									{'naziv': 'predlozi amandman', 'vrednost':'predlozi_amandman'},
									{'naziv': 'unesi dnevni red', 'vrednost':'unesi_dnevni_red'},
									{'naziv': 'unesi rezultate glasanja', 'vrednost':'unesi_rezultate_glasanja'}];

			$scope.listaStatusa = [{'naziv': 'aktivna', 'vrednost':'aktivna'},{'naziv': 'neaktivna', 'vrednost':'neaktivna'}];
			$scope.selectedStanje="";
			$scope.selectedStatus="";


			$scope.fillData = function()
			{
				$scope.currentSessionId = $stateParams.id;
			};

			$scope.fillData();


			$scope.edit_session = function()
			{
				console.log("Vrednost :"+$scope.selectedStanje.vrednost);
				sessionService.edit_session_state($scope.currentSessionId, $scope.selectedStanje.vrednost).then(function(response){
					console.log("Stanje promenjeno...");
					if($scope.selectedStanje.vrednost==='unesi_dnevni_red')
					{
						$state.go('addSessionTimetable', {id: $scope.currentSessionId});
					}
				});
			}

		}
	];