// inject service(Feed) into controllers in order to
// let controllers could communicate between each other
// by sharing and modifying data through service's API.

angular.module("rssApp").controller("sidebarController", ["$scope", "$rootScope", "$state", "$stateParams", "$timeout", "Feed", "Util",
function($scope, $rootScope, $state, $stateParams, $timeout, Feed, Util) {
    // models initialization
    $scope.feeds = [];
    $scope.actions = [
      { name: "today", text: "今日内容" },
      { name: "star", text: "星标内容" },
      { name: "all", text: "全部" },
      //{ name: "category", text: "分类" } // TODO
      ];
    $scope.selectedFeed = null;
    $scope.addTooltipActive = false;
    // state holders
    $scope.refreshingFeed = false;
    $scope.fetchingFeed = false;

    Feed.all(
      function(feeds) {
        $scope.feeds = feeds;
        console.log(feeds);
      },
      function(data, status, headers, config) {
        if (status === 401) {
          Util.relogin(data.code);
        } else {
          var msg = Util.getErrorMessage(data);
          Util.alertError(msg);
        }
      });

  // so many functions...
  $scope.fn = {};

  /* fn.actions */
  $scope.fn.actions = {};
  var actionFuncs = {
    today: Feed.setToday,
    star: Feed.setAllStarred,
    all: Feed.setAll
  };
  ["today", "star", "all"].forEach(function(action) {
    $scope.fn.actions[action] = function() {
      actionFuncs.call(Feed);
      Feed.setArticle(undefined);
      $state.go("dashboard.misc", { action: action }, { reload: true });
    }
  });
  $scope.fn.actions.isActive = function(name) {
    return $stateParams.action === name;
  };

  /* fn.addTooltip */
  $scope.fn.addTooltip = {
    show: function() {
      $scope.addTooltipActive = true;
    },
    hide: function() {
      $scope.addTooltipActive = false;
    }
  };

  /* fn.updateForm */
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
        Util.alertError("No selected Feed");
        return;
      }
      var id = $scope.selectedFeed.id;
      var title = $scope.selectedFeed.title;
      if (!id || !title ) {
        Util.alertError("no id or title");
        return;
      }
      $scope.fn.updateForm.hide();
      Feed.updateFeedTitle(id, title,
        function(data, status, headers, config) {
          Util.alertSuccess("Update title success");
          console.log('update feed title success');
          console.log(data);
        },
        function(data, status, headers, config) {
          if (status === 401) {
            Util.relogin(data.code);
          } else {
            var msg = Util.getErrorMessage(data);
            Util.alertError(msg);
          }
        }
      );
    }
  };

  /* fn.feed */
  $scope.fn.feed = {
    create: function($event) {
      var url = event.target.value;
      if (event.keyCode === 13) {
        if (Util.validateURL(url) && !$scope.fetchingFeed) {
          $scope.fetchingFeed = true;           // lock request
          event.target.value = '';              // reset
          Util.alertInfo("Fetching feed, please wait...", null); // persist notice
          Feed.create(url,
            function(data, status, headers, config) {
              Util.alertSuccess("Fetch success!");
              console.log('update feed title success');
              console.log(data);
              $scope.fetchingFeed = false;      // release lock
              Feed.addFeed(data);
              $timeout(function() {
                $scope.addTooltipActive = false;     // hide tooltip
              }, 0);
            },
            function(data, status, headers, config) {
              if (status === 401) {
                Util.relogin(data.code);
              } else {
                var msg = Util.getErrorMessage(data);
                Util.alertError(msg);
              }
              $scope.fetchingFeed = false;      // release lock
              console.log('update feed title error');
              console.log(data);
              $timeout(function() {
                $scope.addTooltipActive = false;     // hide tooltip
              }, 0);
            }
          );
        }
      } else {
        Util.alertError("Invalid URL");
        console.log("invalid url!");
      }
    },
    set: function(feed) {
      Feed.setFeed(feed);
      Feed.setArticle(undefined); // clear article-detail section
      $state.go("dashboard.feed", { id: feed.id }, { reload: true });
    },
    delete: function(feed) {
      // update views
      console.log(feed.id);
      $scope.fn.updateForm.hide();
      Feed.removeFeed(feed);

      // sync with database
      Feed.delete(feed.id, function(data, status, headers, config) {
        console.log("delete feed successful");
        console.log(data);
        Util.alertSuccess("Delete success!");
      }, function(data, status, headers, config) {
        if (status === 401) {
          Util.relogin(data.code);
        } else {
          var msg = Util.getErrorMessage(data)
          Util.alertError(msg);
        }
        console.log("delete feed failed!!!");
        console.log(data);
      })
    },

    /**
     * Refresh all feeds
     */
    refresh: function() {
      if (!$scope.refreshingFeed) {
        // provide an array of feed ids
        var ids = $scope.feeds.map(function(feed) {
          return feed.id;
        });
        $scope.refreshingFeed = true;         // lock request
        Util.alertInfo("Refreshing...", null);
        Feed.refresh(ids,
          function(data, status, headers, config) {
            Util.alertSuccess("Refresh success!");
            $scope.refreshingFeed = false;    // release lock
            data.forEach(function(d) {
              Feed.addArticles(d.feed_id, d.articles);
            });
            console.log("refresh success");
            console.log(data);
          },
          function(data, status, headers, config) {
            if (status === 401) {
              Util.relogin(data.code);
            } else {
              var msg = Util.getErrorMessage(data);
              Util.alertError(msg);
            }
            console.log("refresh failed");
            $scope.refreshingFeed = false;    // release lock
            console.log(data);
          }
        );
      }
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
