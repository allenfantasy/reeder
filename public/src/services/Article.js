'use strict';

var app = angular.module("rssApp");

app.factory('Article', ["$http", function($http) {
  var fn = {};
  ["read", "unread", "star", "unstar"].forEach(function(action) {
    fn[action] = function(id, success, error) {
      $http.post('api/articles/' + id + '/' + action)
        .success(success).error(error);
    }
  })
  return fn;
}]);
