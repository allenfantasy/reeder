var app = angular.module("rssApp");

app.controller("profileHeaderController", ["$scope", "$rootScope", "$timeout", "$auth",
  function($scope, $rootScope, $timeout, $auth) {
    $scope.settingsTooltipActive = false;
    $scope.showSettings = function() {
      $scope.settingsTooltipActive = true;
    };
    $scope.hideSettings = function() {
      $scope.settingsTooltipActive = false;
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
