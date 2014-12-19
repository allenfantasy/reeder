'use strict'

var rssApp = angular.module('rssApp', [
  'ngRoute',
  'rssControllers'
]);

// routing config
rssApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/', {
        templateUrl: 'partials/homepage.html',
        controller: 'RssHomeCtrl'
      }).
      otherwise({
        redirectTo: '/'
      });
  }]);
