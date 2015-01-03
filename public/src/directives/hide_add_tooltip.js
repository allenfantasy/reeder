var app = angular.module("rssApp");

app.directive('hideAddTooltip', function($document) {
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
