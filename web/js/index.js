var myApp = angular.module('skupstinaApp',['ngRoute', 'ui.bootstrap']);
myApp.controller('loginCtrl', require('./loginController.js'));
myApp.controller('skupstinaCtrl', require('./skupstinaController.js'));
myApp.controller('lawCtrl', require('./lawsController.js'));

myApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/login', {
        templateUrl: './templates/login.html',
        controller: 'loginCtrl'
      }).
      when('/lawsList', {
        templateUrl: './templates/lawsList.html',
        controller: 'lawCtrl'
      }).
      otherwise({
      	redirectTo: '/login'
      });

}]);

myApp
.service('lawsService', require('./lawsService.js'));
