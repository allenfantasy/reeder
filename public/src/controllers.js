'use strict';

/* Controllers */

var rssControllers = angular.module('rssControllers', []);

rssControllers.controller("RssHomeCtrl", ["$scope", function($scope) {
  $scope.content = "homepage";
}]);

rssControllers.controller("MainController", ["$http", function($http) {
  var self = this;
  $http.get('feeds').success(function(data) {
    console.log(data);
    self.feeds = data;
  });
  this.actions = [
    { name: "today", text: "今日内容" },
    { name: "star", text: "星标内容" },
    { name: "all", text: "全部" },
    { name: "category", text: "分类" }
  ];
  //this.feeds = 'abcdefg'.split("");
  this.content = "testing testing lorem ipsum";
}])
