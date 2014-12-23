'use strict';

/* Controllers */

var feedControllers = angular.module('feedControllers', []);

// inject service(Feed) into controllers in order to
// let controllers could communicate between each other
// by sharing and modifying data through service's API.

feedControllers.controller("sidebarController", ["$scope", "$window", "$state", "$stateParams", "Feed",
  function($scope, $window, $state, $stateParams, Feed) {
    $scope.actions = [
      { name: "today", text: "今日内容" },
      { name: "star", text: "星标内容" },
      { name: "all", text: "全部" },
      { name: "category", text: "分类" }
    ];
    $scope.data = Feed.getData();
    $scope.setFeed = function(feed) {
      Feed.setFeed(feed);
      console.log(feed.articles);
      $state.go("feed", { id: feed.id });
    };

    $scope.addTooltipActive = false;

    $scope.showAddTooltip = function() {
      $scope.addTooltipActive = true;
    }

    $scope.hideAddTooltip = function() {
      $scope.addTooltipActive = false;
    }

    /*
     * 1. check if the key pressed is 'Enter'
     * 2. check if the current link is a valid URL
     * 3. sent POST request to create a new Feed
     */
    $scope.tryCreateFeed = function(event) {
      var url = event.target.value;
      //console.log(url);
      if (event.keyCode === 13) {
        console.log("it's a Enter. check the url please");
        if (Feed.validateURL(url)) {
          console.log("valid url. Ready to create Feed");
          Feed.create(url, fetchSuccessCallback, fetchErrorCallback);
        } else {
          // TODO: pop up warning.
          console.log("invalid url....");
        }
      }
    };

    function fetchSuccessCallback(data, status, headers, config) {
      // should add something
      console.log(data);
      console.log(status);
      console.log(headers);
      console.log(config);
      Feed.addFeed(data);
    }

    function fetchErrorCallback(data, status, headers, config) {
      console.log(data);
      console.log(status);
      console.log(headers);
      console.log(config);
    }
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
