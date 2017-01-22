'use strict'

module.exports = [
  '$stateProvider',
  function authConfig($stateProvider) {

    $stateProvider
      .state('login', {
        url : '/login',
        templateUrl: './templates/login.html',
        controller: 'LoginController',
        public: true
      });
  }
];

