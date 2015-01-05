var app = angular.module("rssApp");

var INVALID_TOKEN = 1;
var USER_NOT_FOUND = 2;
var EXPIRED_TOKEN = 3;

app.factory('Util', ["$state", "$auth", "$rootScope", function($state, $auth, $rootScope) {
  return {
    validateURL: function(url) {
      // Credit to http://regexr.com?37i6s
      // Also the SO answer which provide the link above: http://stackoverflow.com/a/3809435/1301194
      var regex = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,4}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g;
      return url.match(regex);
    },
    relogin: function(code) {
      var content = "";
      if (code === INVALID_TOKEN) {
        content = "Invalid token. ";
      } else if (code === USER_NOT_FOUND) {
        content = "User not found. ";
      } else if (code === EXPIRED_TOKEN) {
        content = "Token expired. "
      }
      content += "Please login."
      $auth.removeToken();
      $rootScope.$emit("notice", {
        type: "info",
        content: content
      });
      $state.go("login");
    }
  };
}]);
