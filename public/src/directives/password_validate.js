var app = angular.module("rssApp");

app.directive("passwordValidate", function() {
  return {
    require: "ngModel",
    link: function(scope, elem, attrs, ngModel) {
      ngModel.$validators.tooShort = function(modelValue) {
        return !modelValue || (modelValue.length >= 6);
      };
      ngModel.$validators.tooLong = function(modelValue) {
        return !modelValue || (modelValue.length <= 20);
      };
    }
  }
});
