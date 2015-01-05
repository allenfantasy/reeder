angular.module("rssApp").controller("listController", ["$scope", "$state", "$stateParams", "Feed", "Article",
  function($scope, $state, $stateParams, Feed, Article) {
    $scope.articles = Feed.getArticles();
    $scope.feed = Feed.getFeed();
    $scope.setArticle = function(article) {
      // 将Article本身设为已读
      if (!article.readed) {
        Article.read(article.id);
      }
      article.readed = true;
      // 修改Feed Service中的feeds数据，同步Feed的状态
      Feed.readArticle(article);
      Feed.setArticle(article);
      // 修改路由
      var stateName = $state.$current.name;
      if (stateName === "dashboard.feed") {
        $state.go("dashboard.feed.article",
          { id: article.feed_id, title: article.title },
          { reload: true });
      }
      else if ($stateParams.action) {
        $state.go("dashboard.misc.article",
          { action: $stateParams.action, title: article.title },
          { reload: true });
      }
    };

    $scope.pubDate = function(article) {
      return new Date(article.pub_date);
    }
    $scope.$on('addArticles', function(articles) {
      $scope.articles = $scope.articles.concat(articles);
    });
  }
]);
