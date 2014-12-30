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
    $scope.actions = [
      { name: "today", text: "今日内容" },
      { name: "star", text: "星标内容" },
      { name: "all", text: "全部" },
      //{ name: "category", text: "分类" } // TODO
    ];
    $scope.selectedFeed = null;
    $scope.isRefreshing = false;

    Feed.all(function(feeds) {
      $scope.feeds = feeds;
      console.log(feeds);
    });

    $scope.fn = {
      actions: {
        today: function() {
          console.log("today");
          Feed.setToday();
          $state.go("today");
        },
        star: function() {
          console.log("star");
          Feed.setAllStarred();
          $state.go("star");
        },
        all: function() {
          console.log("all");
          Feed.setAll();
          $state.go("all");
        }
      }
    };

    $scope.setFeed = function(feed) {
      Feed.setFeed(feed);
      Feed.setArticle(undefined); // clear article-detail section
      $scope.t = feed.title;
      $state.go("feed", { id: feed.id });
    };

    $scope.isActiveItem = function(feed) {
      return feed.id + '' === $stateParams.id;
    };

    $scope.unreadNum = function(feed) {
      return feed.articles.filter(function(article) {
        return !article.readed;
      }).length;
    }

    $scope.addTooltipActive = false;

    $scope.showAddTooltip = function() {
      $scope.addTooltipActive = true;
    };

    $scope.hideAddTooltip = function() {
      $scope.addTooltipActive = false;
    };

    $scope.showUpdateForm = function(feed, $event) {
      console.log("show form");

      // prevent event bubbling.
      if ($event.stopPropagation) $event.stopPropagation();

      // compatible with IE 8+
      if ($event.preventDefault) $event.preventDefault();
      $event.cancelBubble = true;
      $event.returnValue = false;

      $scope.selectedFeed = feed;
    };

    $scope.hideUpdateForm = function() {
      $scope.selectedFeed = null;
    }

    $scope.submitUpdateForm = function() {
      if (!$scope.selectedFeed ) {
        // TODO: popup some error tips
        console.log("no selected Feed");
        return;
      }
      var id = $scope.selectedFeed.id;
      var title = $scope.selectedFeed.title;
      if (!id || !title ) {
        // TODO: popup some error tips
        console.log("no id or title");
        return;
      }
      $scope.hideUpdateForm();
      Feed.updateFeedTitle(id, title,
        function(data, status, headers, config) {
          console.log('update feed title success');
          console.log(data);
        },
        function(data, status, headers, config) {
          console.log('update feed title error');
          console.log(data);
        });
    }

    /**
     * Create a feed is input url is valid
     *
     * 1. check if the key pressed is 'Enter'
     * 2. check if the current link is a valid URL
     * 3. sent POST request to create a new Feed
     */
    $scope.tryCreateFeed = function(event) {
      var url = event.target.value;
      if (event.keyCode === 13) {
        console.log("it's a Enter. check the url please");
        if (Feed.validateURL(url)) {
          //  TODO should add some loading tips
          console.log("valid url. Ready to create Feed");
          Feed.create(url,
            function(data, status, headers, config) {
              console.log("create feed success");
              console.log(data);
              Feed.addFeed(data);
            },
            function(data, status, headers, config) {
              console.log("create feed error");
              console.log(data);
            });
        } else {
          // TODO: pop up warning.
          console.log("invalid url....");
        }
      }
    };

    /**
     * Refresh all feeds
     * All ! All feeds !
     */
    $scope.refresh = function() {
      $scope.isRefreshing = true;
      // provide an array of feed ids
      var ids = $scope.feeds.map(function(feed) {
        return feed.id;
      });
      Feed.refresh(ids,
        function(data, status, headers, config) {
          console.log("refresh success");
          $scope.isRefreshing = false;
          console.log(data);
          // [ { feed_id: xx1, articles: yy1 }, { feed_id: xx2, articles: yy2 }]
          data.forEach(function(d) {
            Feed.addArticles(d.feed_id, d.articles);
          });
        },
        function(data, status, headers, config) {
          console.log("refresh failed");
          $scope.isRefreshing = false;
          console.log(data);
          // TODO
        }
      );
    }
  }
]);

feedControllers.controller("listController", ["$scope", "$state", "$stateParams", "Feed", "Article",
  function($scope, $state, $stateParams, Feed, Article) {
    $scope.articles = Feed.getArticles();
    $scope.feed = Feed.getFeed();
    $scope.setArticle = function(article) {
      Feed.setArticle(article);
      // 将Article本身设为已读
      if (!article.readed) {
        Article.read(article.id);
      }
      article.readed = true;
      // 修改Feed Service中的feeds数据，同步Feed的状态
      Feed.readArticle(article);
      // 修改路由
      $state.go("article", { id: $scope.feed.id, title: article.title })
    };

    $scope.pubDate = function(article) {
      return new Date(article.pub_date);
    }
  }
]);

feedControllers.controller("detailController", ["$scope", "$sce", "$stateParams", "Feed", "Article",
  function($scope, $sce, $stateParams, Feed, Article) {
    $scope.article = Feed.getArticle();
    $scope.renderHtml = function(html) {
      return $sce.trustAsHtml(html);
    };
    $scope.toggleReadState = function() {
      // 更新list中的article的状态
      // 更新sidebar中article对应的feed的状态
      $scope.article.readed = !$scope.article.readed;
      if ($scope.article.readed) {
        Article.read($scope.article.id);
        Feed.readArticle($scope.article);
      } else {
        Article.unread($scope.article.id);
        Feed.unreadArticle($scope.article);
      }
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

feedControllers.controller("headerController", ["$scope", "Feed",
  function($scope, Feed) {
    $scope.settingsTooltipActive = false;
    $scope.read = function() {
      // TODO add loading tips?
      Feed.readFeed();
    }
  }
]);
