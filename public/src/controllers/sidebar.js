// inject service(Feed) into controllers in order to
// let controllers could communicate between each other
// by sharing and modifying data through service's API.



angular.module("rssApp").controller("sidebarController", ["$scope", "$rootScope", "$window", "$state", "$stateParams", "Feed", "Util",
function($scope, $rootScope, $window, $state, $stateParams, Feed, Util) {
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
    $scope.addTooltipActive = false;

    Feed.all(
      function(feeds) {
        $scope.feeds = feeds;
        console.log(feeds);
      },
      function(data, status, headers, config) {
        if (status == 401) {
          Util.relogin(data.code);
        }
      });

  // so many functions...
  $scope.fn = {}
  $scope.fn.actions = {
    today: function() {
      Feed.setToday();
      Feed.setArticle(undefined); // clear article-detail section
      $state.go("dashboard.misc", { action: 'today' }, { reload: true });
    },
    star: function() {
      Feed.setAllStarred();
      Feed.setArticle(undefined); // clear article-detail section
      $state.go("dashboard.misc", { action: 'star' }, { reload: true });
    },
    all: function() {
      Feed.setAll();
      Feed.setArticle(undefined); // clear article-detail section
      $state.go("dashboard.all", { action: 'all' }, { reload: true });
    },
    isActive: function(name) {
      return $stateParams.action === name;
    }
  };
  $scope.fn.addTooltip = {
    show: function() {
      $scope.addTooltipActive = true;
    },
    hide: function() {
      $scope.addTooltipActive = false;
    }
  };
  $scope.fn.updateForm = {
    show: function(feed, $event) {
      console.log("show form");

      // prevent event bubbling.
      if ($event.stopPropagation) $event.stopPropagation();

      // compatible with IE 8+
      if ($event.preventDefault) $event.preventDefault();
      $event.cancelBubble = true;
      $event.returnValue = false;

      $scope.selectedFeed = feed;
    },
    hide: function() {
      $scope.selectedFeed = null;
    },
    submit: function() {
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
      $scope.fn.updateForm.hide();
      Feed.updateFeedTitle(id, title,
        function(data, status, headers, config) {
          console.log('update feed title success');
          console.log(data);
        },
        function(data, status, headers, config) {
          if (status === 401) {
            // TODO: delete token & go back to login page
          }
          console.log('update feed title error');
          console.log(data);
        }
      );
    }
  };

  $scope.fn.feed = {
    create: function($event) {
      var url = event.target.value;
      if (event.keyCode === 13) {
        console.log("it's a Enter. check the url please");
        if (Util.validateURL(url)) {
          // block other create request...
          console.log("valid url. Ready to create Feed");
          Feed.create(url,
            function(data, status, headers, config) {
              console.log('update feed title success');
              console.log(data);
              Feed.addFeed(data);
            },
            function(data, status, headers, config) {
              if (status === 401) {
                // TODO: clear token, back to login
              }
              console.log('update feed title error');
              console.log(data);
            }
          );
        }
      } else {
        // TODO popup warning
        console.log("invalid url!");
      }
    },
    set: function(feed) {
      Feed.setFeed(feed);
      Feed.setArticle(undefined); // clear article-detail section
      //$scope.t = feed.title;
      $state.go("dashboard.feed", { id: feed.id }, { reload: true });
    },
    delete: function(feed) {
      // update views
      console.log(feed.id);
      $scope.fn.updateForm.hide();
      Feed.removeFeed(feed);

      // sync with database
      Feed.delete(feed.id, function(data, status, headers, config) {
        // TODO: notice
        console.log("delete feed successful");
        console.log(data);
        $rootScope.$emit("notice", {
          type: "success",
          content: "Delete success!"
        });
      }, function(data, status, headers, config) {
        // error
        console.log("delete feed failed!!!");
        console.log(data);
      })
    },
    /**
     * Refresh all feeds
     * All ! All feeds !
     */
    refresh: function() {
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
          //console.log($state.$current.name);
          // [ { feed_id: xx1, articles: yy1 }, { feed_id: xx2, articles: yy2 }]
          data.forEach(function(d) {
            Feed.addArticles(d.feed_id, d.articles);
          });
        },
        function(data, status, headers, config) {
          if (status === 401) {
            // TODO: clear token, back to login
          }
          console.log("refresh failed");
          $scope.isRefreshing = false;
          console.log(data);
          // TODO
        }
      );
    },
    isActive: function(feed) {
      return feed.id + '' === $stateParams.id;
    },
    unreadNum: function(feed) {
      var num = feed.articles.filter(function(article) {
        return !article.readed;
      }).length;
      return num == 0 ? '' : '(' + num + ')';
    }
  };
}]);
