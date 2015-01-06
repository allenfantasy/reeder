var app = angular.module("rssApp");

var EXPIRED_TOKEN = 3;

app.controller("ProfileController",["$scope", "$timeout", "$auth", "Account", "Util",
  function($scope, $timeout, $auth, Account, Util) {
    var errorHandler = function(data, status) {
      if (status === 401) {
        Util.relogin(data.code);
      } else {
        var msg = Util.getErrorMessage(data);
        Util.alertError(msg);
      }
    }
    $scope.getProfile = function() {
      Account.getProfile()
        .success(function(data) {
          $scope.user = data;
          Util.alertSuccess("Get profile success!");
          console.log(data);
        })
        .error(errorHandler);
    };

    $scope.updateProfile = function() {
      Account.updateProfile({ name: $scope.user.name })
        .success(function(data) {
          Util.alertSuccess("Update profile success");
          console.log(data);
        })
        .error(errorHandler);
    };

    $scope.updatePassword = function() {
      Account.updatePassword({ password: $scope.password })
        .success(function(data) {
          Util.alertSuccess("Update password success");
          $scope.password = '';
          $scope.confirmPassword = '';

          // update token, or relogin
          if (data.token) {
            $auth.setToken(data.token, true); // seems like a bug..issued submitted
            // reset form
            $scope.passwordForm.password.$setPristine();
            $scope.passwordForm.confirmPassword.$setPristine();
          } else {
            Util.relogin(EXPIRED_TOKEN);
          }

          console.log(data);
        })
        .error(errorHandler);
    }

    $scope.hasError = function(formName, fieldName) {
      return formName[fieldName].$invalid && formName[fieldName].$dirty;
    }

    $scope.getProfile();
  }]);
