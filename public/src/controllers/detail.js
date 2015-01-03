angular.module("rssApp").controller("detailController", ["$scope", "$sce", "$stateParams", "Feed", "Article",
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
