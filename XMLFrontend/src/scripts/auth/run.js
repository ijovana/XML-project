'use strict';

module.exports = [
  '$rootScope',
  '$state',
  'loginService',
  function setupRouterSecurity($rootScope, $state, loginService) {

    function routeChecker(event, next, current) {
        console.log("here");
      var loggedIn = loginService.isLoggedIn();

      if (loggedIn) {
          console.log("it's logged in, ok");
        loginService.attachToken(loggedIn);
      }

      if (!loggedIn && !next.public) {
          console.log("should login");
        event.preventDefault();
        $state.go('login');
      }

      if (loggedIn && next.public) {
          console.log(":P");
        event.preventDefault();
      }

    }

    $rootScope.$on('$stateChangeStart', routeChecker);
      $rootScope.logout = loginService.logOut;
  }
];
