'use strict';

/* Controllers */

var feedControllers = angular.module('feedControllers', []);

// inject service(Feed) into controllers in order to
// let controllers could communicate between each other
// by sharing and modifying data through service's API.

feedControllers.controller("sidebarController", ["$scope", "$state", "$stateParams", "Feed",
  function($scope, $state, $stateParams, Feed) {
    $scope.actions = [
      { name: "today", text: "今日内容" },
      { name: "star", text: "星标内容" },
      { name: "all", text: "全部" },
      { name: "category", text: "分类" }
    ];
    $scope.data = Feed.getData();
    $scope.setFeed = function(feed) {
      Feed.setFeed(feed);
      $state.go("feed", { id: feed.id });
    };
  }
]);

feedControllers.controller("listController", ["$scope", "$state", "$stateParams", "Feed", "Article",
  function($scope, $state, $stateParams, Feed, Article) {
    $scope.data = Feed.getData();
    $scope.setArticle = function(article) {
      Feed.setArticle(article);
      //console.log(article);
      if (!article.readed) {
        Article.read(article.id);
      }
      article.readed = true;
      $state.go("article", { id: $scope.data.feed.id, title: article.title })
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
