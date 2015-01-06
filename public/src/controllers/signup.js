var app = angular.module("rssApp");

app.controller("SignupController",["$scope", "$state", "$stateParams", "$auth",
  function($scope, $state, $stateParams, $auth) {
    // OMG... refer to http://stackoverflow.com/a/46181/1301194
    $scope.emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    $scope.signup = function() {
      /*console.log('displayName:', $scope.displayName);
      console.log('email:', $scope.email);
      console.log('password:', $scope.password)*/
      $auth.signup({
        username: $scope.displayName,
        email: $scope.email,
        password: $scope.password
      }).then(function(response) {
        alert("Signup success! Please login.");
        console.log("signup success");
        console.log(response.data);
      },function(response) {
        alert(response.data.message);
        console.log("signup failed");
        console.log(response.data);
      });
    }
  }]);
