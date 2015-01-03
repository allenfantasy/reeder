angular.module("rssApp").controller("listController", ["$scope", "$state", "$stateParams", "Feed", "Article",
  function($scope, $state, $stateParams, Feed, Article) {
    $scope.articles = Feed.getArticles();
    $scope.feed = Feed.getFeed();
    $scope.setArticle = function(article) {
      Feed.setArticle(article);
      // 将Article本身设为已读
      if (!article.readed) {
        Article.read(article.id);
      }
      article.readed = true;
      // 修改Feed Service中的feeds数据，同步Feed的状态
      Feed.readArticle(article);
      // 修改路由
      $state.go("dashboard.article", { id: $scope.feed.id, title: article.title })
    };

    $scope.pubDate = function(article) {
      return new Date(article.pub_date);
    }
    $scope.$on('addArticles', function(articles) {
      $scope.articles = $scope.articles.concat(articles);
    });
  }
]);
