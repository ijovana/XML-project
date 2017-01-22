'use strict';

module.exports = [
  '$http',
  '$q',
  '$window',
  function loginService($http, $q, $window) {

    function attachToken(token) {
      $http.defaults.headers.common.Authorization = token;
    }

    function authenticate(token) {
        console.log("setting token!");
        attachToken(token);
        $window.sessionStorage.authToken = token;
    }

    function isLoggedIn() {
        console.log("checking login: " + $window.sessionStorage.authToken);
      return $window.sessionStorage.authToken;
    }

    function logIn(user) {
        console.log("try login");

        var url = 'http://localhost:8080/XML-project/auth/login';
        return $http( {
            url: url,
            method: 'POST',
            transformResponse: [function(data) { return data; }],
            data: user
        })
        .then(function(response) {
            console.log("login successful");
          authenticate(response.data);
        });
    }

    function logOut() {
      delete $http.defaults.headers.common.Authorization;
      delete $window.sessionStorage.authToken;
    }

    return {
      logIn: logIn,
      attachToken: attachToken,
      isLoggedIn: isLoggedIn,
      logOut: logOut
    };
  }
];
