// inject service(Feed) into controllers in order to
// let controllers could communicate between each other
// by sharing and modifying data through service's API.

// TODO: should move these into constants
var INVALID_TOKEN = 1;
var USER_NOT_FOUND = 2;
var EXPIRED_TOKEN = 3;

angular.module("rssApp").controller("sidebarController", ["$scope", "$window", "$state", "$stateParams", "Feed",
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
    $scope.addTooltipActive = false;

    console.log(Feed.alreadyFetched);
    Feed.all(
      function(feeds) {
        $scope.feeds = feeds;
        console.log(feeds);
        console.log(Feed.alreadyFetched);
      },
      function(data, status, headers, config) {
        if (status == 401) {
      console.log("401: " + data.message);
      if (data.code === INVALID_TOKEN) {
        // TODO
      } else if (data.code === USER_NOT_FOUND) {
        // TODO
      } else if (data.code === EXPIRED_TOKEN) {
        // TODO
      }
    }
  });

  $scope.fn = {
    actions: {
      today: function() {
        console.log("today");
        Feed.setToday();
        $state.go("dashboard.today");
      },
      star: function() {
        console.log("star");
        Feed.setAllStarred();
        $state.go("dashboard.star");
      },
      all: function() {
        console.log("all");
        Feed.setAll();
        $state.go("dashboard.all");
      },
      isActive: function(name) {
        return $state.$current.name == name;
      }
    },
    addTooltip: {
      show: function() {
        $scope.addTooltipActive = true;
      },
      hide: function() {
        $scope.addTooltipActive = false;
      }
    },
    updateForm: {
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
      },
      feed: {
        create: function($event) {
          var url = event.target.value;
          if (event.keyCode === 13) {
            console.log("it's a Enter. check the url please");
            if (Feed.validateURL(url)) {
              //  TODO should add some loading tips
              console.log("valid url. Ready to create Feed");
              Feed.create(url,
                function(data, status, headers, config) {
                  console.log('update feed title success');
                  console.log(data);
                  Feed.addFeed(data);
                },
                function(data, status, headers, config) {
                  console.log('update feed title error');
                  console.log(data);
                });
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
            $state.go("dashboard.feed", { id: feed.id });
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
            return feed.articles.filter(function(article) {
              return !article.readed;
            }).length;
          }
        }
      };
    }
    ]);
