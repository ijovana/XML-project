require("./auth/app.js");

var myApp = angular.module('skupstinaApp',['ngRoute', 'ui.bootstrap', 'angularTrix', 'ui.router', 'checklist-model', 'skupstinaApp.auth']);
// myApp.controller('loginCtrl', require('./loginController.js'));
myApp.controller('skupstinaCtrl', require('./skupstinaController.js'));
myApp.controller('lawCtrl', require('./lawsController.js'));
myApp.controller('addRegulationCtrl', require('./addRegulationController.js'));
myApp.controller('addAmendmentCtrl', require('./addAmendmentController.js'));
myApp.controller('addSessionCtrl', require('./addSessionController.js'));
myApp.controller('createNewSessionCtrl', require('./createNewSessionController.js'));
myApp.controller('editSessionDetailsCtrl', require('./editSessionDetailsController.js'));
myApp.controller('addSessionTimetableCtrl', require('./addSessionTimetableController.js'));
myApp.controller('addTimetableCtrl', require('./addTimetableController.js'));
myApp.controller('insertSessionResultsCtrl', require('./insertSessionResultsController.js'));

myApp.config(['$stateProvider','$urlRouterProvider',
  function($stateProvider,$urlRouterProvider) {

    // $urlRouterProvider.otherwise("/login");
      // .state('login', {
      //   url: '/login',
      //   templateUrl: './templates/login.html',
      //   controller: 'loginCtrl'
      // })

    $stateProvider
      .state('addRegulation', {
        url: '/addRegulation',
        templateUrl: './templates/addRegulation.html',
        controller: 'addRegulationCtrl'
      })
       .state('addAmendment', {
        url: '/addAmendment',
        templateUrl: './templates/addAmendment.html',
        controller: 'addAmendmentCtrl'
      })
      .state('addSession', {
        url: '/addSession',
        templateUrl: './templates/createNewSession.html',
        controller: 'createNewSessionCtrl'
      })
       .state('editSession', {
        url: '/editSession/:id',
        templateUrl: './templates/editSessionDetails.html',
        controller: 'editSessionDetailsCtrl'
      })
      .state('addSessionTimetable', {
        url: '/addSessionTimetable/:id',
        templateUrl: './templates/addTimetable.html',
        controller: 'addTimetableCtrl'
      })
      .state('insertSessionResults', {
        url: '/insertSessionResults/:id',
        templateUrl: './templates/insertSessionResults.html',
        controller: 'insertSessionResultsCtrl'
      })
      .state('regulationsList', {
        url: '/regulationsList',
        templateUrl: './templates/lawsList.html',
        controller: 'lawCtrl'
      });

}]);


myApp
.service('lawsService', require('./lawsService.js'))
.service('amendmentService', require('./amendmentService.js'))
.service('sessionService', require('./sessionService.js'));
