'use strict';

/* Controllers */

var feedControllers = angular.module('feedControllers', []);

// inject service(Feed) into controllers in order to
// let controllers could communicate between each other
// by sharing and modifying data through service's API.

feedControllers.controller("sidebarController", ["$scope", "$window", "$state", "$stateParams", "Feed",
  function($scope, $window, $state, $stateParams, Feed) {
    // models initialization
    $scope.feeds = [];
    $scope.currentFeed = null;
    $scope.actions = [
      { name: "today", text: "今日内容" },
      { name: "star", text: "星标内容" },
      { name: "all", text: "全部" },
      { name: "category", text: "分类" }
    ];

    Feed.all(function(feeds) {
      $scope.feeds = feeds;
      //console.log(feeds);
    });

    $scope.setFeed = function(feed) {
      Feed.setFeed(feed);
      $scope.currentFeed = feed;
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
    $scope.articles = Feed.getArticles();
    $scope.feed = Feed.getFeed();
    $scope.setArticle = function(article) {
      Feed.setArticle(article);
      //console.log(article);
      if (!article.readed) {
        Article.read(article.id);
      }
      article.readed = true;
      // check if the feed's articles are all read. Update feed's "cleared" attr if so.
      var feed = Feed.getFeed();
      var cleared = true;
      for(var i = 0; i < feed.articles.length; i++) {
        if (!feed.articles[i].readed) {
          cleared = false;
          break;
        }
      }
      Feed.setFeedCleared();
      //if (cleared) feed.cleared = true;
      $state.go("article", { id: $scope.feed.id, title: article.title })
    };
  }
]);

feedControllers.controller("detailController", ["$scope", "$sce", "$stateParams", "Feed", "Article",
  function($scope, $sce, $stateParams, Feed, Article) {
    $scope.article = Feed.getArticle();
    $scope.renderHtml = function(html) {
      return $sce.trustAsHtml(html);
    };
    $scope.toggleReadState = function() {
      $scope.article.readed = !$scope.article.readed;

      var feed = Feed.getFeed();

      if ($scope.article.readed) {
        Article.read($scope.article.id);
        var cleared = true;
        for(var i = 0; i < feed.articles.length; i++) {
          if (!feed.articles[i].readed) {
            cleared = false;
            break;
          }
        }
        if (cleared) feed.cleared = true;
      } else {
        Article.unread($scope.article.id);
        //Feed.setFeedCleared();
        feed.cleared = false;
      }

      var allFeeds = Feed.getFeeds();
      console.log(feed);
      console.log(allFeeds);
    };
    $scope.toggleStarState = function() {
      $scope.article.starred = !$scope.article.starred;

      if ($scope.article.starred) {
        Article.star($scope.article.id);
      } else {
        Article.unstar($scope.article.id);
      }
    };
  }
]);
