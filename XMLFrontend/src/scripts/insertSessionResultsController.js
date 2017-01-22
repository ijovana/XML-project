module.exports = [
	'$scope', '$http', 'amendmentService', 'lawsService', 'sessionService', '$state','$stateParams', '$window',
	function addSessionController($scope, $http, amendmentService, lawsService, sessionService, $state, $stateParams, $window){
		
		$scope.fillData = function()
		{
			$scope.currentSessionId = $stateParams.id;
			sessionService.get_session($scope.currentSessionId).then(function(response){
				$scope.sessionXML = response;
				console.log("SESSION DATA: "+$scope.sessionXML);
				$scope.start();
			});
		};

		$scope.fillData();


		$scope.start = function()
		{
			var editor=document.getElementById("editor");
			Xonomy.render($scope.sessionXML, editor, null);
		};


		$scope.harvest = function()
		{
			//console.log("Harvest xonomy..");
			$scope.xmlResult = Xonomy.harvest();
			console.log("Xml result: "+$scope.xmlResult);
		};

		$scope.save_results = function()
		{
			$scope.harvest();
			sessionService.edit_session_results($scope.currentSessionId, $scope.xmlResult).then(function(response){
				console.log('Results inserted...');
			});
		};

	}
];