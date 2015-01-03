'use strict'

var rssApp = angular.module('rssApp', [
  'ui.router',
  'ngSanitize',
  'ngMessages',
  'satellizer',
  'feedService',
  'feedFilters'
]);

// routing config
rssApp.config(function($stateProvider, $urlRouterProvider, $locationProvider) {
  // default & 404
  //$urlRouterProvider.otherwise('/dashboard');
  $urlRouterProvider.otherwise('/signup');

  // TODO ready to refactor this
  var dashboardViews = {
    "sidebar": {
      templateUrl: "partials/sidebar.html",
      controller: "sidebarController"
    },
    "article-list": {
      templateUrl: "partials/article_list.html",
      controller: "listController"
    },
    "article-detail": {
      templateUrl: "partials/article_detail.html",
      controller: "detailController"
    },
    "header-ctrl@": {
      templateUrl: "partials/header_ctrl.html",
      controller: "headerController"
    }
  };

  $stateProvider
    .state("home", {
      // TODO some public fancy homepage like intro page
      url: "/home",
      template: "<h1>This is public</h1>",
      controller: function($scope) {}
    })
    .state("login", {
      url: "/login",
      templateUrl: "partials/login.html",
      controller: "LoginController"
    })
    .state("signup", {
      url: "/signup",
      templateUrl: "partials/signup.html",
      controller: "SignupController"
    })
    .state("logout", {
      url: "/logout",
      template: null,
      controller: "LogoutController"
    })
    .state("profile", {
      url: "/profile",
      template: "partials/profile.html",
      controller: "ProfileController"
    })
    .state("dashboard", { // already logged in
      url: "/dashboard",
      abstract: true,
      templateUrl: "partials/dashboard.html"
    })
    .state("dashboard.index", {
      url: '',
      views: dashboardViews
    })
    .state("dashboard.feed", {
      url: "/feed/:id",
      views: dashboardViews
    })
    .state("dashboard.article", {
      url: "/feed/:id/article/:title",
      views: dashboardViews
    })
    .state("dashboard.today", {
      url: "/today",
      views: dashboardViews
    })
    .state("dashboard.star", {
      url: "/star",
      views: dashboardViews
    })
    .state("dashboard.all", {
      url: "/all",
      views: dashboardViews
    });

});
