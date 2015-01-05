'use strict';

var app = angular.module("rssApp");

app.filter('fromNow', function() {
  return function(date) {
    return moment(date).fromNow();
  }
});
