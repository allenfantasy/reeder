angular.module("rssApp")
  .directive('passwordStrength', function() {
    function calPasswordStrength(pwd) {
      var matches = {
        positive: {},
        negative: {}
      },
      counts = {
        positive: {},
        negative: {}
      },
      tmp,
      strength = 0,
      letters = 'abcdefghijklmnopqrstuvwxyz',
      numbers = '01234567890',
      symbols = '\\!@#$%&/()=?¿',
      strValue;

      // Increase strength level
      matches.positive.lower = pwd.match(/[a-z]/g);
      matches.positive.upper = pwd.match(/[A-Z]/g);
      matches.positive.numbers = pwd.match(/\d/g);
      matches.positive.symbols = pwd.match(/[$-/:-?{-~!^_`\[\]]/g);
      matches.positive.middleNumber = pwd.slice(1, -1).match(/\d/g);
      matches.positive.middleSymbol = pwd.slice(1, -1).match(/[$-/:-?{-~!^_`\[\]]/g);

      counts.positive.lower = matches.positive.lower ? matches.positive.lower.length : 0;
      counts.positive.upper = matches.positive.upper ? matches.positive.upper.length : 0;
      counts.positive.numbers = matches.positive.numbers ? matches.positive.numbers.length : 0;
      counts.positive.symbols = matches.positive.symbols ? matches.positive.symbols.length : 0;

      counts.positive.numChars = pwd.length;
      tmp += (counts.positive.numChars >= 8) ? 1 : 0;

      counts.positive.requirements = (tmp >= 3) ? tmp : 0;
      counts.positive.middleNumber = matches.positive.middleNumber ? matches.positive.middleNumber.length : 0;
      counts.positive.middleSymbol = matches.positive.middleSymbol ? matches.positive.middleSymbol.length : 0;

      // Decrease strength level
      matches.negative.consecLower = pwd.match(/(?=([a-z]{2}))/g);
      matches.negative.consecUpper = pwd.match(/(?=([A-Z]{2}))/g);
      matches.negative.consecNumbers = pwd.match(/(?=(\d{2}))/g);
      matches.negative.onlyNumbers = pwd.match(/^[0-9]*$/g);
      matches.negative.onlyLetters = pwd.match(/^([a-z]|[A-Z])*$/g);

      counts.negative.consecLower = matches.negative.consecLower ? matches.negative.consecLower.length : 0;
      counts.negative.consecUpper = matches.negative.consecUpper ? matches.negative.consecUpper.length : 0;
      counts.negative.consecNumbers = matches.negative.consecNumbers ? matches.negative.consecNumbers.length : 0;

      // Calculations
      strength += counts.positive.numChars * 4;
      if (counts.positive.upper) {
        strength += (counts.positive.numChars - counts.positive.upper) * 2;
      }
      if (counts.positive.lower) {
        strength += (counts.positive.numChars - counts.positive.lower) * 2;
      }
      if (counts.positive.upper || counts.positive.lower) {
        strength += counts.positive.numbers * 4;
      }
      strength += counts.positive.symbols * 6;
      strength += (counts.positive.middleSymbol + counts.positive.middleNumber) * 2;
      strength += counts.positive.requirements * 2;

      strength -= counts.negative.consecLower * 2;
      strength -= counts.negative.consecUpper * 2;
      strength -= counts.negative.consecNumbers * 2;

      if (matches.negative.onlyNumbers) {
        strength -= counts.positive.numChars;
      }
      if (matches.negative.onlyLetters) {
        strength -= counts.positive.numChars;
      }

      strength = Math.max(0, Math.min(100, Math.round(strength)));
      return strength;
    }

    return {
      restrict: 'A',
      require: 'ngModel',
      link: function(scope, element, attrs, ngModel) {
        var indicator = element.children();

        var form = element.parent().parent();

        var dots = Array.prototype.slice.call(indicator.children());
        var weakest = dots.slice(-1)[0];
        var weak = dots.slice(-2);
        var strong = dots.slice(-3);
        var strongest = dots.slice(-4);

        element.after(indicator);

        function updateDots() {
          angular.forEach(dots, function(el) {
            el.style.backgroundColor = '#ebeef1';
          });

          if (ngModel.$viewValue) {
            var strength = calPasswordStrength(ngModel.$viewValue);
            if (strength > 85) {
              angular.forEach(strongest, function(el) {
                el.style.backgroundColor = '#008cdd';
              });
            } else if (strength > 65) {
              angular.forEach(strong, function(el) {
                el.style.backgroundColor = '#6ead09';
              });
            } else if (strength > 30) {
              angular.forEach(weak, function(el) {
                el.style.backgroundColor = '#e09115';
              });
            } else {
              weakest.style.backgroundColor = '#e01414';
            }
          }
        }
        function resetDots() {
          angular.forEach(dots, function(el) {
            el.style.backgroundColor = '#ebeef1';
          });
        }
        form.bind('submit', resetDots);
        element.bind('keyup', updateDots);

        ngModel.$validators.weak = function(modelValue) {
          return !modelValue || (calPasswordStrength(modelValue) > 30);
        }
      },
      template: '<span class="password-strength-indicator"><span></span><span></span><span></span><span></span></span>'
    };
  });
