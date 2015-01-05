var app = angular.module("rssApp");

app.directive('hideTooltip', function($document) {
  return {
    restrict: 'A',
    link: function(scope, elem, attr, ctrl) {
      // prevent this elem to do anything -- include hiding.
      // (because this event can not pop up to document)
      elem.bind('click', function(e) {
        e.stopPropagation();
      });
      // bind all elements click event to hide tooltip
      // But, it seems if we attach 'hide-tooltip' to N elements,
      // $apply would be called N times [yes, i've just confirmed this fact]
      $document.bind('click', function() {
        scope.$apply(attr.hideTooltip);
      })
    }
  }
});
