module.exports = [
	'$scope', '$http','amendmentService', 'lawsService', '$routeParams','$window',
	function myController($scope, $http, amendmentService, lawsService ,$routeParams, $window){


		function fillData()
		{
			console.log("Add amendments controller!");
			var x2js = new X2JS();

	        lawsService.get_all_laws().then(function(response){
	        	$scope.allRegulations = response;
	        	$scope.allRegulationsJSON = x2js.xml2js($scope.allRegulations);
	        	$scope.propisi = $scope.allRegulationsJSON.propiss;
	        	console.log("PROPISI: "+JSON.stringify($scope.propisi.propis[0].deo));

	        	$scope.myArray = [];
	       		for(var i=0; i<$scope.propisi.propis.length; i++)
	        	{
	        		var id = $scope.propisi.propis[i]._ID;
	        		var naziv = "Naziv zakonika";
	        		$scope.myArray.push({'_naziv': naziv, '_ID': id});
	        	}

	        	console.log($scope.myArray);
	        	$scope.listaOdredbiPropisa = $scope.myArray;
	        });

	        $scope.odeljci = [];
	        $scope.clanovi = [];
	        $scope.selectedOdeljak = "";
	        $scope.selectedPropis="";
	        $scope.selectedClan = "";
	        $scope.selectedOdredbaPropisa ="";


		};

		$scope.start = function() {
			var xml = "<ime><nesto>Kristina</nesto></ime>";

			$scope.xmlTest= "<am:amandman stanje='predlozen' redni_broj='1.' naziv='Naziv amandmana' status='HF8_3gFGRUG6dfM8HUGcE94Q8WfX' about='JPTsKH4C_F4zTc7Q09U9Au' xsi:schemaLocation='http://www.parlament.gov.rs/amandman file:/C:\Users\Mima\Documents\GitHub\apache-tomee-plus-1.7.4/_data/schemas/amandman.xsd' xmlns='http://www.w3.org/ns/rdfa#' xmlns:pr='http://www.parlament.gov.rs/propisi' xmlns:xs='http://www.w3.org/2001/XMLSchema#' xmlns:pred='http://www.parlament.gov.rs/predicate' xmlns:am='http://www.parlament.gov.rs/amandman' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>"
						+	"<am:naziv_predloga property='pred:naziv_predloga' datatype='xs:string'>"
						+		"Uneti naziv predloga amandmana..."
						+	"</am:naziv_predloga>"
						+	"<am:odredba_predloga>"
						+		$scope.selectedOdredbaPropisa._ID
						+	"</am:odredba_predloga>"
						+	"<am:predlozeno_resenje>"
						+		"<am:brisanje>"
						+			"false"
						+		"</am:brisanje>"
						+		"<am:izmena>"
						+			"false"
						+		"</am:izmena>"
						+		"<am:dopuna>"
						+			"true"
						+		"</am:dopuna>"
						+	"</am:predlozeno_resenje>"
						+ 	"<am:obrazlozenje>"
						+		"Uneti obrazlozenje predlaganja amandmana..."
						+	"</am:obrazlozenje>"
						+	"<am:podnosilac>"
						+		"<am:ime>"
						+ 			"Uneti ime..."
						+		"</am:ime>"
						+		"<am:prezime>"
						+			"Uneti prezime"
						+		"</am:prezime>"
						+	"</am:podnosilac>"
						+	"<am:sadrzaj>"
						+		"Dopuniti sadrzaj amandmana..."
						+	"</am:sadrzaj>"
						+ 	"<am:datum_donosenja property='pred:datum_donosenja' datatype='xs:string'>"
						+		"2018-05-15Z"
						+   "</am:datum_donosenja>"
						+	"<am:broj_glasova_za property='pred:broj_glasova_za' datatype='xs:int'>"
						+		"0"
						+	"</am:broj_glasova_za>"
						+	"<am:mesto property='pred:mesto' datatype='xs:string'>"
						+		"Novi Sad"
						+	"</am:mesto>"
						+	"<am:broj_glasova_protiv property='pred:broj_glasova_protiv' datatype='xs:int'>"
						+		"0"
						+	"</am:broj_glasova_protiv>"
						+	"<am:datum_kreiranja prototype='pred:datum_kreiranja' datatype='xs:date'>"
						+		"2017-05-30"
						+	"</am:datum_kreiranja>"
						+"</am:amandman>";


			var docSpecTest={
				onchange: function(){	
					console.log("Changed now!")
				},
				validate: function(obj){
					console.log("Validated now!")	
				},	

				elements: {
					"am:amandman":{
						attributes: 
						{
							"naziv": {
								asker: Xonomy.askString
							},
							"redni_broj": {
								asker: Xonomy.askString
							},
							"about": {
								asker: Xonomy.askString
							}
						}
					},

					

				}
			};
			var editor=document.getElementById("editor");
			Xonomy.render($scope.xmlTest, editor, docSpecTest);

		};

		$scope.harvest = function()
		{
			console.log("Harvest xonomy..");
			$scope.xmlResult = Xonomy.harvest();
			console.log("Xml result: "+$scope.xmlResult);
		};

		$scope.regulation_selection_changed = function()
		{
			console.log("Selection changed..."+$scope.selectedPropis);
			$scope.odeljci = [];
			$scope.clanovi = [];

			amendmentService.get_sectionIds($scope.selectedPropis._ID).then(function(response){
				$scope.odeljciXML = response;
				var x2js = new X2JS();
				$scope.odeljciJSON= x2js.xml2js($scope.odeljciXML);
				console.log("JSON odeljci: "+JSON.stringify($scope.odeljciJSON));
				var resLen = $scope.odeljciJSON.database.result.length;
				if(resLen==undefined)
				{
					var naziv = $scope.odeljciJSON.database.result.naziv._naziv;
	        		var id = $scope.odeljciJSON.database.result.id._ID;
	        		$scope.odeljci.push({'_naziv': naziv, '_ID': id});
				}
				else{
					for(var i=0; i<$scope.odeljciJSON.database.result.length; i++)
					{
						var naziv = $scope.odeljciJSON.database.result[i].naziv._naziv;
		        		var id = $scope.odeljciJSON.database.result[i].id._ID;
		        		$scope.odeljci.push({'_naziv': naziv, '_ID': id});
					}
				}

			});
		}

		$scope.odeljak_selection_changed = function()
		{
			console.log("Odeljak selection changed, object id: "+$scope.selectedOdeljak._ID);
			amendmentService.get_articleIds($scope.selectedOdeljak._ID).then(function(response){
				$scope.clanoviXML = response;
				console.log("JSON clanovi: "+$scope.clanoviXML);
				var x2js = new X2JS();
				$scope.clanoviJSON= x2js.xml2js($scope.clanoviXML);
				console.log("JSON clanovi: "+JSON.stringify($scope.clanoviJSON));

				var resLen = $scope.clanoviJSON.database.result.length;
				if(resLen==undefined)
				{
					var naziv = $scope.clanoviJSON.database.result.naziv._naziv;
	        		var id = $scope.clanoviJSON.database.result.id._ID;
	        		$scope.clanovi.push({'_naziv': naziv, '_ID': id});
				}
				else{
					for(var i=0; i<$scope.clanoviJSON.database.result.length; i++)
					{
						var naziv = $scope.clanoviJSON.database.result[i].naziv._naziv;
		        		var id = $scope.clanoviJSON.database.result[i].id._ID;
		        		$scope.clanovi.push({'_naziv': naziv, '_ID': id});
					}
				}


			});

		}


		$scope.clan_selection_changed = function()
		{
			console.log("Clan selection changed.."+$scope.selectedClan._ID);
		}

		$scope.set_selection = function()
		{
			console.log("Set selection");
			if($scope.selectedPropis!= undefined && $scope.selectedPropis!="") 
			{
				$scope.selectedOdredbaPropisa = $scope.selectedPropis;
			}

			if($scope.selectedOdeljak!=undefined && $scope.selectedOdeljak!="")
			{
				$scope.selectedOdredbaPropisa = $scope.selectedOdeljak;
			}
			if($scope.selectedClan!=undefined && $scope.selectedClan!="")
			{
				$scope.selectedOdredbaPropisa = $scope.selectedClan;
			}

			$scope.start();
		}


		$scope.add_amendment = function()
		{
			$scope.harvest();
			console.log("SENDING DATA...");
			$scope.send_amendment($scope.xmlResult);
		};

		$scope.send_amendment = function(data)
		{
			amendmentService.create_amendment(data).then(function(response){
				console.log("Amendment maybe finished...");
			});
		}

		fillData();
		$scope.start();
	}
];