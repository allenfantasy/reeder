angular.module("rssApp").controller("listController", ["$scope", "$rootScope", "$state", "$stateParams", "Feed", "Article", "Util",
  function($scope, $rootScope, $state, $stateParams, Feed, Article, Util) {
    $scope.articles = Feed.getArticles();
    $scope.feed = Feed.getFeed();
    $scope.setArticle = function(article) {
      // 将Article本身设为已读
      if (!article.readed) {
        Article.read(article.id, function(data, status) {
          Util.alertSuccess("Read success!");
          console.log('read success');
          console.log(data);
        }, function(data, status) {
          if (status === 401) {
            Util.relogin(data.code);
          } else {
            var msg = Util.getErrorMessage(data);
            Util.alertError(msg);
          }
          console.log('read failure');
          console.log(data);
        });
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
