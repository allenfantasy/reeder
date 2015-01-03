var app = angular.module("rssApp");

app.controller("SignupController",["$scope", "$state", "$stateParams", "$auth",
  function($scope, $state, $stateParams, $auth) {
    $scope.signup = function() {
      console.log('displayName:', $scope.displayName);
      console.log('email:', $scope.email);
      console.log('password:', $scope.password)
      $auth.signup({
        displayName: $scope.displayName,
        email: $scope.email,
        password: $scope.password
      }).catch(function(response) {
        // TODO ...
      });
    }
  }]);
