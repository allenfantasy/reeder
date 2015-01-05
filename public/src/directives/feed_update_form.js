
var app = angular.module("rssApp");

app.directive('feedUpdateForm', function($document) {
  var linkFunc = function(scope, elem, attrs) {};
  return {
    restrict: 'E',
    link: linkFunc,
    templateUrl: "partials/sidebar/feed_update_form.html"
  };
});
