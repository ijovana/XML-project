module.exports = [
	'$scope', '$http','lawsService', '$routeParams','$window',
	function myController($scope, $http, lawsService, $routeParams, $window){

		$scope.data = "CAO KRISTINA!!! ";
		$scope.content="";
		$scope.textXML="TextXML";
		$scope.xmlResult = "";

		function fillData()
		{
			console.log("Add regulations controller!");
		};

		$scope.start = function() {
			console.log("Start xonomy..");
			var xmlTest="<pr:propis stanje='predlozen' xsi:schemaLocation='http://www.parlament.gov.rs/propisi file://home/kristina/Desktop/XMLProj/apache-tomee-plus-1.7.4/_data/schemas/propis.xsd' xmlns='http://www.w3.org/ns/rdfa#' "
						+	" xmlns:pred='http://www.parlament.gov.rs/predicate/' xmlns:pr='http://www.parlament.gov.rs/propisi' xmlns:xs='http://www.w3.org/2001/XMLSchema#' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'> "
						+	"<pr:deo naziv='Naziv dela' redni_broj='1.'>"
						+ 		"<pr:glava broj='1.' naslov='Naslov glave'>"
						+ 			"<pr:odeljak redni_broj='1.' naziv='Naziv odeljka'>"
						+ 				"<pr:clan broj='1.' naziv='Naziv clana'>"
						+					"<pr:naslov>"
						+						"Naslov clana"	
						+					"</pr:naslov>"	
						+					"<pr:opis>"
						+						"Opis clana"				
						+					"</pr:opis>"
						+					"<pr:sadrzaj>"
						+						"<pr:stav id='Id stava'>"
						+							"<pr:tacka naziv='Naziv tacke' >"
						+								"<pr:podtacka naziv='Naziv podtacke'>"
						+									"<pr:alineje>"
						+										"Alineja ima tekst.."
						+									"</pr:alineje>"
						+								"</pr:podtacka>"
						+ 							"</pr:tacka>"			
						+						"</pr:stav>"
						+					"</pr:sadrzaj>"
						+ 				"</pr:clan>"
						+ 			"</pr:odeljak>"
						+ 		"</pr:glava>"
						+	"</pr:deo>"
						+	"<pr:naziv>"
						+		"Naziv zakonika"
						+	"</pr:naziv>"
						+  "<pr:mesto property='pred:mesto' datatype='xs:string'>"
						+		"Unesi mesto"
						+	"</pr:mesto>"
						+	"<pr:datum_donosenja property='pred:datum_donosenja' datatype='xs:date'>"
						+		"2016-06-20"
						+	"</pr:datum_donosenja>"
						+"</pr:propis>";

			var editor=document.getElementById("editor");

			var docSpecTest={
				onchange: function(){	
					console.log("I been changed now!")
				},
				validate: function(obj){
					console.log("I be validatin' now!")	
				},	
				elements: {
					"pr:propis": {
						menu: [{
							caption: "Append an <pr:deo>",
							action: Xonomy.newElementChild,
							actionParameter: "<pr:deo xmlns:pr='http://www.parlament.gov.rs/propisi'></pr:deo>"
						}],

						attributes: 
						{
							"naziv": {
								asker: Xonomy.askString
							}
						}
					},

					"pr:deo": {
						menu: [{
							caption: "Append an <pr:glava>",
							action: Xonomy.newElementChild,
							actionParameter: "<pr:glava xmlns:pr='http://www.parlament.gov.rs/propisi' broj='1.' naslov='default naslov'></pr:glava>"
						},
						{
							caption: "Delete this <pr:deo>",
							action: Xonomy.deleteElement
						}],

						attributes: 
						{
							"naziv": {
								asker: Xonomy.askString
							}, 
					
							"redni_broj": {
								asker: Xonomy.askString	
							}
						}
					}, 

					"pr:glava": {
						menu: [{
							caption: "Append an <pr:odeljak>",
							action: Xonomy.newElementChild,
							actionParameter: "<pr:odeljak xmlns:pr='http://www.parlament.gov.rs/propisi' redni_broj='1.' naziv='Naziv odeljka'></pr:odeljak>"
						},
						{
							caption: "Delete this <pr:glava>",
							action: Xonomy.deleteElement
						}],

						attributes: 
						{
							"broj": {
								asker: Xonomy.askString
							}, 
					
							"naslov": {
								asker: Xonomy.askString	
							}
						}
					},

					"pr:odeljak": {
						menu: [{
								caption: "Append an <pr:clan>",
								action: Xonomy.newElementChild,
								actionParameter: "<pr:clan xmlns:pr='http://www.parlament.gov.rs/propisi' broj='1.' naziv='Naziv clana'></pr:clan>"
							}, {
								caption: "Append an <pr:odeljak>",
								action: Xonomy.newElementChild,
								actionParameter: "<pr:odeljak xmlns:pr='http://www.parlament.gov.rs/propisi' redni_broj='1.' naziv='Naziv odeljka'></pr:odeljak>"
							},
							{
								caption: "Delete this <pr:odeljak>",
								action: Xonomy.deleteElement
						}],

						attributes: 
						{
							"redni_broj": {
								asker: Xonomy.askString
							}, 
					
							"naziv": {
								asker: Xonomy.askString	
							}
						}
					}, 

					"pr:clan": {
						menu: [{
								caption: "Delete this <pr:clan>",
								action: Xonomy.deleteElement
						}],

						attributes: 
						{
							"broj": {
								asker: Xonomy.askString
							}, 
					
							"naziv": {
								asker: Xonomy.askString	
							}
						}
					},

					"pr:sadrzaj": {
						menu: [{
							caption: "Append an <pr:stav>",
							action: Xonomy.newElementChild,
							actionParameter: "<pr:stav xmlns:pr='http://www.parlament.gov.rs/propisi' id='1.'></pr:stav>"
						},
						{
							caption: "Delete this <pr:sadrzaj>",
							action: Xonomy.deleteElement
						}]
					}, 

					"pr:stav": {
						menu: [{
							caption: "Append an <pr:tacka>",
							action: Xonomy.newElementChild,
							actionParameter: "<pr:tacka xmlns:pr='http://www.parlament.gov.rs/propisi' naziv='Naziv tacke'></pr:tacka>"
						},
						{
								caption: "Delete this <pr:stav>",
								action: Xonomy.deleteElement
						}],

						attributes: 
						{
							"id": {
								asker: Xonomy.askString
							}
						}
					}, 

					"pr:tacka": {
						menu: [{
							caption: "Append an <podtacka>",
							action: Xonomy.newElementChild,
							actionParameter: "<pr:podtacka xmlns:pr='http://www.parlament.gov.rs/propisi' naziv='Naziv podtacke'></pr:podtacka>"
						},{
								caption: "Delete this <pr:tacka>",
								action: Xonomy.deleteElement
						}],

						attributes: 
						{
							"naziv": {
								asker: Xonomy.askString
							}
						}
					}, 


					"pr:podtacka": {
						menu: [{
							caption: "Append an <pr:alineje>",
							action: Xonomy.newElementChild,
							actionParameter: "<pr:alineje xmlns:pr='http://www.parlament.gov.rs/propisi'>Alineja ima tekst..</pr:alineje>"
						},
						{
								caption: "Delete this <pr:podtacka>",
								action: Xonomy.deleteElement
						}],

						attributes: 
						{
							"naziv": {
								asker: Xonomy.askString
							}
						}
					} 
				}
			};

			console.log("XML: "+xmlTest);
			
			Xonomy.render(xmlTest, editor, docSpecTest);
		};

		$scope.harvest = function()
		{
			console.log("Harvest xonomy..");
			$scope.xmlResult = Xonomy.harvest();
			console.log("Xml result: "+$scope.xmlResult);
		};

		$scope.add_regulation = function()
		{
			$scope.harvest();
			console.log("SENDING DATA...");
			$scope.send_regulation($scope.xmlResult);
		};

		$scope.send_regulation = function(data)
		{
			lawsService.create_law(data).then(function(response){
				console.log("CREATE MAYBE FINISHED...");
			});			
		};



		fillData();
		$scope.start();

	}
];