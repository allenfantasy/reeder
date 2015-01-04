var app = angular.module("rssApp");

app.controller("SignupController",["$scope", "$state", "$stateParams", "$auth",
  function($scope, $state, $stateParams, $auth) {
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
