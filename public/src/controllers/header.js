angular.module("rssApp").controller("headerController", ["$scope", "$rootScope", "$state", "$timeout", "Feed", "Util",
  function($scope, $rootScope, $state, $timeout, Feed, Util) {
    $scope.settingsTooltipActive = false;
    $scope.listNotEmpty = Feed.getArticles().length > 0;
    //console.log($state.$current.name);
    //console.log(Feed.getFeed());
    $scope.showSettings = function() {
      $scope.settingsTooltipActive = true;
    };
    $scope.hideSettings = function() {
      $scope.settingsTooltipActive = false;
    };
    $scope.read = function() {
      Feed.readArticles(function(data, status, headers, config) {
        // success
        console.log('mark as read success');
        console.log(data);
      }, function(data, status, headers, config) {
        // error
        console.log(status, data.message);
      });
    };

    // control notice
    // notice types: info, success, danger
    $rootScope.$on("notice", function(event, notice) {
      $timeout(function() {
        $scope.notice = notice;
      }, 0);
      if (notice.duration) { // hide
        //console.log("hide after some duration");
        $timeout(function() {
          $scope.notice = null;
        }, notice.duration * 1000);
      } else {
        //console.log("no duration. should persist");
      }
    });
  }
]);
