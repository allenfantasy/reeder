var app = angular.module("rssApp");

app.directive('targetBlank',function() {
  return {
    restrict: 'A',
    link: function(scope, elem, attr, ctrl) {
      scope.$evalAsync(function($scope) {
        var $elem = $(elem);
        $(elem).find("a").attr('target', '_blank');
      });
    }
  }
});
