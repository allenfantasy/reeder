angular.module("rssApp").controller("headerController", ["$scope", "$state", "Feed",
  function($scope, $state, Feed) {
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
  }
]);
