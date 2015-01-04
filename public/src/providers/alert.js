var app = angular.module("rssApp");

app.provider("$alert", function() {
  // preparation
  var defaults = this.defaults = {
    // TODO: add defaults
  };

  // the constructor of provider
  this.$get = ["$rootScope", function($rootScope) {
    var $alert = {};

    // Common vars
    var options = $alert.options = angular.extend({}, defaults, config);
    $alert.$promise = 
  }];
})
