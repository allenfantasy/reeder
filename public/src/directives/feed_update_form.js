
var app = angular.module("rssApp");

app.directive('feedUpdateForm', function($document) {
  var linkFunc = function(scope, elem, attrs) {
    // TODO: inject scope's current feed's title into <input>
  }
  return {
    restrict: 'E',
    link: linkFunc,
    templateUrl: "partials/sidebar/feed_update_form.html"
  };
});
