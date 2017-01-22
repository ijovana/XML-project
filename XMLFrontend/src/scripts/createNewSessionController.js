	module.exports = [
	'$scope', '$http', 'amendmentService', 'lawsService', 'sessionService', '$state', '$stateParams', '$window',
	function sessionController($scope, $http, amendmentService, lawsService, sessionService, $state, $stateParams, $window){

		$scope.create_new_session = function()
		{
			var xml = "<sed:sednica xsi:schemaLocation='http://www.parlament.gov.rs/sednica file:/C:\Users\Mima\Documents\GitHub\apache-tomee-plus-1.7.4/_data/schemas/sednica.xsd' "
							+	" xmlns:xs='http://www.w3.org/2001/XMLSchema#' " 
							+	" xmlns:sed='http://www.parlament.gov.rs/sednica' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>"
							+	"<sed:stanje>"
							+		"predlozi_propis"
							+	"</sed:stanje>"
							+	"<sed:status>"
							+		"aktivna"
							+	"</sed:status>"
							+	"<sed:glasanje>"
							+		"<sed:predmet_glasanja>"
							+			"Unesi predmet glasanja"
							+		"</sed:predmet_glasanja>"
							+		"<sed:broj_glasova_za>"
							+			"0"
							+		"</sed:broj_glasova_za>"
							+		"<sed:broj_glasova_protiv>"
							+			"0"
							+		"</sed:broj_glasova_protiv>"
							+		"<sed:broj_glasova_uzdrzani>"
							+			"0"
							+		"</sed:broj_glasova_uzdrzani>"
							+	"</sed:glasanje>"
							+"</sed:sednica>";

			sessionService.create_session(xml).then(function(response){
				// TODO: redirect to /sessionDetails/sessionId -> edit stanje -> kad je stanje create timetable
				// onda preusmeri na /createTimetable/sessionId , a kad je rezultat glasanja, onda na /insertResults..
				console.log("Session created "+response);
				var x2js = new X2JS();
				$scope.createdSession = x2js.xml2js(response);
				console.log("Created session JSON: "+JSON.stringify($scope.createdSession));
				var id = $scope.createdSession.sednica._ID;
				console.log("Session id: "+JSON.stringify(id));
				$state.go('editSession', {id: id});
			});
		};

		}
	];