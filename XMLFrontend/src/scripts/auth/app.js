'use strict';

angular.module('skupstinaApp.auth',[
  require('angular-ui-router'),
])

  .config(require('./config'))

  .controller('LoginController', require('./controller.js'))

  .service('loginService', require('./service.js'))

  .run(require('./run.js'))

  .factory('authInterceptor', require('./interceptor.js'));
