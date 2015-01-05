var app = angular.module("rssApp");

var INVALID_TOKEN = 1;
var USER_NOT_FOUND = 2;
var EXPIRED_TOKEN = 3;

app.factory('Util', ["$state", "$auth", "$rootScope", function($state, $auth, $rootScope) {
  var defaults = {
    ALERT_DURATION: 1
  };

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

      // BUG: seems this not working..
      this.alertInfo(content, null); // persistent info
      $state.go("login");
    },
    alert: function(type, content, duration) {
      $rootScope.$emit("notice", {
        type: type,
        content: content,
        duration: (duration !== undefined ? duration : defaults.ALERT_DURATION)
      });
    },
    alertSuccess: function(content, duration) {
      this.alert("success", content, duration);
    },
    alertError: function(content, duration) {
      this.alert("danger", content, duration);
    },
    alertInfo: function(content, duration) {
      this.alert("info", content, duration);
    },
    getErrorMessage: function(data) {
      if (data && data.message) {
        return data.message;
      } else {
        return "Network error";
      }
    }
  };
}]);
