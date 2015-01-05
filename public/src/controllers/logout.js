var app = angular.module("rssApp");

app.controller("LogoutController",["$scope", "$state", "$stateParams", "$auth",
  function($scope, $state, $stateParams, $auth) {
    if (!$auth.isAuthenticated()) return;

    $auth.logout()
      .then(function() {
        alert("You've been logged out!");
      });
  }]);
