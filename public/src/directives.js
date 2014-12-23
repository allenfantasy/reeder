'use strict';

var feedDirectives = angular.module('feedDirectives', []);

feedDirectives.directive('resize', function($window) {
  return function(scope, element, attr) {
    var w = angular.element($window);
    scope.$watch(function() {
      return {
        'h': w.height(),
        'w': w.width()
      };
    }, function(newValue, oldValue) {
      scope.windowHeight = newValue.h;
      scope.windowWidth = newValue.w;

      var HEADER_HEIGHT = 54;
      var ARTICLE_LIST_GAP = 20 * 2;
      scope.resizeWithOffset = function() {
        scope.$eval(attr.notifier);

        return {
          'height': newValue.h - HEADER_HEIGHT
        };
      }
      scope.listResizeWithOffset = function() {
        scope.$eval(attr.notifier);

        return {
          'height': newValue.h - HEADER_HEIGHT - ARTICLE_LIST_GAP
        };
      }
    }, true);

    w.bind('resize', function() {
      scope.$apply();
    });
  };
});

feedDirectives.directive('hideAddTooltip', function($document) {
  return {
    restrict: 'A',
    link: function(scope, elem, attr, ctrl) {
      elem.bind('click', function(e) {
        e.stopPropagation();
      });
      $document.bind('click', function() {
        scope.$apply(attr.hideAddTooltip);
      })
    }
  }
});
