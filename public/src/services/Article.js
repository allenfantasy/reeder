'use strict';

var app = angular.module("rssApp");

app.factory('Article', ["$http", function($http) {
  // TODO: Add callbacks
  return {
    read: function(id) {
      $http.post('api/articles/' + id + '/read').success(function(data) {
        console.log('read success');
        console.log(data);
      });
    },
    unread: function(id) {
      $http.post('api/articles/' + id + '/unread').success(function(data) {
        console.log('unread success');
        console.log(data);
      });
    },
    star: function(id) {
      $http.post('api/articles/' + id + '/star').success(function(data) {
        console.log('star success');
        console.log(data);
      });
    },
    unstar: function(id) {
      $http.post('api/articles/' + id + '/unstar').success(function(data) {
        console.log('unstar success');
        console.log(data);
      });
    }
    // TODO: read batch
  };
}]);
