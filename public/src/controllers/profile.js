var app = angular.module("rssApp");

app.controller("ProfileController",["$scope", "$timeout", "Account", "Util",
  function($scope, $timeout, Account, Util) {
    $scope.getProfile = function() {
      Account.getProfile()
        .success(function(data) {
          $scope.user = data;
          Util.alertSuccess("Get profile success!");
          console.log(data);
        })
        .error(function(data, status) {
          if (status === 401) {
            Util.relogin(data.code);
          } else {
            var msg = Util.getErrorMessage(data);
            Util.alertError(msg);
          }
        });
    };

    $scope.updateProfile = function() {
      Account.updateProfile({ name: $scope.user.name })
        .success(function(data) {
          Util.alertSuccess("Update profile success");
          console.log(data);
        })
        .error(function(data, status) {
          if (status === 401) {
            Util.relogin(data.code);
          } else {
            var msg = Util.getErrorMessage(data);
            Util.alertError(msg);
          }
        });
    }

    $scope.getProfile();
  }]);
