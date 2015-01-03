angular.module("rssApp").controller("headerController", ["$scope", "Feed",
  function($scope, Feed) {
    $scope.settingsTooltipActive = false;
    $scope.read = function() {
      // TODO add loading tips?
      Feed.readFeed();
    }
  }
]);
