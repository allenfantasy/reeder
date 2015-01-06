var app = angular.module("rssApp");

app.factory("Account", ["$http", "$auth", function($http, $auth) {
  return {
    getProfile: function() {
      return $http.get("api/me");
    },
    updateProfile: function(data) {
      return $http.put("api/me", data);
    },
    updatePassword: function(data) {
      return $http.put("api/update_password", data);
    }
  };
}]);
