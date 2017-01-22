(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
var myApp = angular.module('skupstinaApp',['ngRoute', 'ui.bootstrap']);
myApp.controller('loginCtrl', require('loginController.js'));
myApp.controller('skupstinaCtrl', require('skupstinaController.js'));
myApp.controller('lawCtrl', require('lawsController.js'));

// debug
console.log('loading file: sve.js...');

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

},{"lawsController.js":2,"loginController.js":3,"skupstinaController.js":4}],2:[function(require,module,exports){
module.exports = [
	'$scope', '$http',
	function myController($scope, $http){

	}
];
},{}],3:[function(require,module,exports){
arguments[4][2][0].apply(exports,arguments)
},{"dup":2}],4:[function(require,module,exports){
arguments[4][2][0].apply(exports,arguments)
},{"dup":2}]},{},[1]);
