	module.exports = [
	'$scope', '$http', 'amendmentService', 'lawsService', 'sessionService', 
	function sessionController($scope, $http, amendmentService, lawsService, sessionService){

		function fillData()
		{
			console.log("Session controller...");
			var x2js = new X2JS();

	        lawsService.get_all_laws().then(function(response){
	        	$scope.allRegulations = response;
	        	$scope.allRegulationsJSON = x2js.xml2js($scope.allRegulations);
	        	$scope.propisi = $scope.allRegulationsJSON.propiss;
	        	console.log("PROPISI: "+JSON.stringify($scope.propisi.propis[0].deo));

	        	$scope.myArray = [];
	        	$scope.test = "[";
	       		for(var i=0; i<$scope.propisi.propis.length; i++)
	        	{
	        		var id = $scope.propisi.propis[i]._ID;
	        		var naziv = "Naziv zakonika";
	        		$scope.myArray.push({caption: id , value: naziv});
	        		$scope.test +="{caption: '"+id+"' , value: '"+naziv+"'}";
	        		if(i!=$scope.propisi.propis.length-1)
	        		{
	        			$scope.test += ",";
	        		}
	        	}
	        	$scope.test +="]";

	        	$scope.listaPropisa = JSON.stringify($scope.myArray);
	        	console.log("My array "+ JSON.stringify($scope.myArray));
	        	console.log("TEST "+$scope.test);

	        	$scope.start();

	        });
		};


		$scope.start = function()
		{
			$scope.xmlTest = "<sed:sednica xsi:schemaLocation='http://www.parlament.gov.rs/sednica file:/C:\Users\Mima\Documents\GitHub\apache-tomee-plus-1.7.4/_data/schemas/sednica.xsd' "
							+	" xmlns:xs='http://www.w3.org/2001/XMLSchema#' " 
							+	" xmlns:sed='http://www.parlament.gov.rs/sednica' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>"
							+	"<sed:stanje>"
							+		"Unesi rezultate glasanja"
							+	"</sed:stanje>"
							+	"<sed:status>"
							+		"Aktivna"
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


			var docSpecTest={
				onchange: function(){	
					console.log("I been changed now!")
				},
				validate: function(obj){
					console.log("I be validatin' now!")	
				},	
				elements: {
					"sed:sednica": {
						menu: [{
							caption: "Append an <sed:glasanje>",
							action: Xonomy.newElementChild,
							actionParameter: "<sed:glasanje xmlns:sed='http://www.parlament.gov.rs/sednica' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'></sed:glasanje>"
						}]
					},

					"sed:glasanje": {
						menu:[
						{
							caption: "Append an <sed:predmet_glasanja>",
							action: Xonomy.newElementChild,
							actionParameter: "<sed:predmet_glasanja xmlns:sed='http://www.parlament.gov.rs/sednica' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'></sed:predmet_glasanja>"
						},
						{
							caption: "Append an <sed:broj_glasova_za>",
							action: Xonomy.newElementChild,
							actionParameter: "<sed:broj_glasova_za xmlns:sed='http://www.parlament.gov.rs/sednica' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'></sed:broj_glasova_za>"
						},
						{
							caption: "Append an <sed:broj_glasova_protiv>",
							action: Xonomy.newElementChild,
							actionParameter: "<sed:broj_glasova_protiv xmlns:sed='http://www.parlament.gov.rs/sednica' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'></sed:broj_glasova_protiv>"
						},

						{
							caption: "Append an <sed:broj_glasova_uzdrzani>",
							action: Xonomy.newElementChild,
							actionParameter: "<sed:broj_glasova_uzdrzani xmlns:sed='http://www.parlament.gov.rs/sednica' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'></sed:broj_glasova_uzdrzani>"
						}	
						]
					},

					"sed:predmet_glasanja":
					{
						asker: Xonomy.askPicklist,
						askerParameter: $scope.listaPropisa
					}
				}
			};


			var editor=document.getElementById("editor");
			Xonomy.render($scope.xmlTest, editor, docSpecTest);
		};

		fillData();

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
			});
		};
		
	}
];	