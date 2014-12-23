'use strict';

var feedFilters = angular.module('feedFilters', []);

feedFilters.filter('fromNow', function() {
  return function(date) {
    return moment(date).fromNow();
  }
})
