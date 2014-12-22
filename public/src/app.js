'use strict'

var rssApp = angular.module('rssApp', [
  'ui.router',
  'ngSanitize',
  'feedService',
  'feedControllers'
]);

// routing config
rssApp.config(function($stateProvider, $urlRouterProvider, $locationProvider) {
  // default & 404
  $urlRouterProvider.otherwise("/");

  var viewsMap = {
    "sidebar": {
      templateUrl: "partials/sidebar.html",
      controller: "sidebarController as sidebar"
    },
    "article-list": {
      templateUrl: "partials/article_list.html",
      controller: "listController as list"
    },
    "article-detail": {
      templateUrl: "partials/article_detail.html",
      controller: "detailController as detail"
    }
  };

  $stateProvider
    .state("default", {
      url: "/",
      views: viewsMap
    })
    .state("feed", {
      url: "/feed/:id",
      views: viewsMap
    })
    .state("article", {
      url: "/feed/:id/article/:title",
      views: viewsMap
    })
});
