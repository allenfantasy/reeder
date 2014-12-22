'use strict';

/* Controllers */

var feedControllers = angular.module('feedControllers', []);

// inject service(Feed) into controllers in order to
// let controllers could communicate between each other
// by sharing and modifying data through service's API.

feedControllers.controller("sidebarController", ["$scope", "$stateParams", "Feed",
  function($scope, $stateParams, Feed) {
    $scope.actions = [
      { name: "today", text: "今日内容" },
      { name: "star", text: "星标内容" },
      { name: "all", text: "全部" },
      { name: "category", text: "分类" }
    ];
    $scope.data = Feed.getData();
    $scope.setArticles = function(articles) {
      Feed.setArticles(articles);
    };
  }
]);

feedControllers.controller("listController", ["$scope", "$stateParams", "Feed",
  function($scope, $stateParams, Feed) {
    $scope.data = Feed.getData();
    $scope.setArticle = function(article) {
      Feed.setArticle(article);
    };
  }
]);

feedControllers.controller("detailController", ["$scope", "$sce", "$stateParams", "Feed",
  function($scope, $sce, $stateParams, Feed) {
    $scope.data = Feed.getData();
    $scope.renderHtml = function(html) {
      return $sce.trustAsHtml(html);
    }
  }
]);
