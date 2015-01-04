var app = angular.module("rssApp");

app.controller("LoginController",["$scope", "$state", "$stateParams", "$auth",
  function($scope, $state, $stateParams, $auth) {
    $scope.login = function() {
      $auth.login({ email: $scope.email, password: $scope.password })
        // Angular Promise API
        // then(successCallback, errorCallback, notifyCallback);
        .then(function(response) {
          alert("login success!");
          console.log("login success");
          console.log(response.data);
        }, function(response) {
          alert(response.data.message);
          console.log("login failed");
          console.log(response.data);
        });
    }
  }]);
