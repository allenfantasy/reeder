'use strict';

/* Controllers */

var rssControllers = angular.module('rssControllers', []);

rssControllers.controller("RssHomeCtrl", ["$scope", function($scope) {
  $scope.content = "homepage";
}]);

rssControllers.controller("MainController", ["$http", function($http) {
  $http.get('feeds').success(function(data) {
    console.log(data);
    //this.feeds = data;
  });
  this.sidebar = "sidebar";
  this.feeds = 'abcdefg'.split("");
  this.content = "testing testing lorem ipsum";
}])
