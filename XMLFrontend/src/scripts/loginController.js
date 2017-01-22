module.exports = [
	'$scope', '$http', '$location',
	function myController($scope, $http, $location){
			
			console.log("HERE! ");
			$scope.tryLogin = function(){
				console.log("EVERYBODY CAN LOGIN NOW !...");	
				var newUrl = "/lawsList";
				console.log(newUrl);
				$location.path(newUrl);	
			};
	}
];
