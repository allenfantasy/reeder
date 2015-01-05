angular.module("rssApp").controller("detailController", ["$scope", "$rootScope", "$sce", "$stateParams", "Feed", "Article", "Util",
  function($scope, $rootScope, $sce, $stateParams, Feed, Article, Util) {
    $scope.article = Feed.getArticle();
    $scope.renderHtml = function(html) {
      return $sce.trustAsHtml(html);
    };
    $scope.toggleReadState = function() {
      // 更新list中的article的状态
      // 更新sidebar中article对应的feed的状态
      $scope.article.readed = !$scope.article.readed;
      if ($scope.article.readed) {
        Article.read($scope.article.id, function(data, status) {
          Util.alertSuccess("Read success!");
        }, function(data, status) {
          if (status === 401) {
            Util.relogin(data.code);
          } else {
            var msg = Util.getErrorMessage(data);
            Util.alertError(msg);
          }
        });
        Feed.readArticle($scope.article);
      } else {
        Article.unread($scope.article.id, function(data, status) {
          Util.alertSuccess("Unread success!");
        }, function(data, status) {
          if (status === 401) {
            Util.relogin(data.code);
          } else {
            var msg = Util.getErrorMessage(data);
            Util.alertError(msg);
          }
        });
        Feed.unreadArticle($scope.article);
      }
    };
    $scope.toggleStarState = function() {
      $scope.article.starred = !$scope.article.starred;

      if ($scope.article.starred) {
        Article.star($scope.article.id, function(data, status, headers, config) {
          Util.alertSuccess("Star success!");
          console.log('star success');
          //console.log(data);
        }, function(data, status, headers, config) {
          // revert when failed
          $scope.article.starred = !$scope.article.starred;

          var msg = Util.getErrorMessage(data);
          Util.alertError(msg);
          console.log('star failure');
          //console.log(data);
        });
      } else {
        Article.unstar($scope.article.id, function(data, status, headers, config) {
          Util.alertSuccess("Unstar success");
          console.log('unstar success');
          console.log(data);
        }, function(data, status, headers, config) {
          // revert when failed
          $scope.article.starred = !$scope.article.starred;

          var msg = Util.getErrorMessage(data);
          Util.alertError(msg);
          console.log('unstar failure');
          console.log(data);
        });
      }
    };
  }
  ]);
