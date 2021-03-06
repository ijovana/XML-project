'use strict'

module.exports = [
  '$scope',
  'loginService',
  '$state',

  function loginController($scope, loginService, $state) {

    $scope.user = {
      email: '',
      password: ''
    };

    $scope.login = function() {
        var payload = {
            authenticationData : {
                username: $scope.user.email,
                password: $scope.user.password
            }
        };
      // perform a login using service
      loginService.logIn(payload)
      .then(
        function loginSuccess() {
          // if login is sucessful, redirect to home
          $state.go('addRegulation');
        }
      );
    };
  }
];
