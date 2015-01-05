'use strict'

var rssApp = angular.module('rssApp', [
  'ui.router',
  'ngSanitize',
  'ngMessages',
  'satellizer'
]);

rssApp.config(function($stateProvider, $urlRouterProvider, $locationProvider, $authProvider) {
  // auth configuration
  $authProvider.loginOnSignup = false;
  $authProvider.loginRedirect = '/dashboard';
  $authProvider.logoutRedirect = '/login';
  $authProvider.signupRedirect = '/login';
  $authProvider.loginUrl = '/api/login';
  $authProvider.signupUrl = '/api/signup';
  $authProvider.tokenName = 'token';
  $authProvider.tokenPrefix = 'reeder'; // Local Storage name prefix
  $authProvider.authHeader = 'Authorization';

  // routing configuration

  // default & 404
  $urlRouterProvider.otherwise('/dashboard');

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
    // TODO some public fancy homepage like intro page
    /*.state("home", {
      url: "/home",
      template: "<h1>This is public</h1>",
      controller: function($scope) {}
    })*/
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
      templateUrl: "partials/dashboard.html",
      resolve: {
        authenticated: function($q, $location, $auth) {
          var deferred = $q.defer();
          if (!$auth.isAuthenticated()) {
            $location.path("/login");
          } else {
            deferred.resolve();
          }
          return deferred.promise;
        }
      }
    })
    .state("dashboard.index", {
      url: '',
      views: dashboardViews
    })
    .state("dashboard.feed", {
      url: "/feed/:id",
      views: dashboardViews
    })
    .state("dashboard.feed.article", {
      url: "/article/:title",
      views: dashboardViews
    })
    .state('dashboard.misc', {
      url: '/:action',
      views: dashboardViews
    })
    .state('dashboard.misc.article', {
      url: '/:title',
      views: dashboardViews
    });
});
