var app = angular.module("rssApp");

app.directive("emailUnique",["$http", function($http) {
  return {
    restrict: "A",
    require: "ngModel",
    link: function(scope, elem, attrs, ngModel) {
      // TODO: Implement this later.

      /*ngModel.$validators.unique = function(modelValue) {
        if (!modelValue) return true;
        if (ngModel.$valid) { // check email's existence only when it is valid
          $http.post("api/check_email_exist", { email: modelValue }).success(function(data, status, headers, config) {
            return false;
          }).error(function(data, status, headers, config) {
            return true;
          });
        }
        else {
          console.log("not valid???");
          return true;
        }
      }*/
    }
  };
}]);
