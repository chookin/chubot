// http://plnkr.co/edit/YyO1dQpFDnzSTLqaOllc?p=info
angular.module('chu.toggle', [])
    .directive('toggleCheckbox', function($timeout) {

        /**
         * Directive
         */
        return {
            restrict: 'A',
            transclude: true,
            replace: false,
            require: 'ngModel',
            link: function ($scope, $element, $attr, ngModel) {

                // update model from Element
                var updateModelFromElement = function() {
                    // If modified
                    var checked = $element.prop('checked');
                    if (checked != ngModel.$viewValue) {
                        // Update ngModel
                        ngModel.$setViewValue(checked);
                        $scope.$apply();
                    }
                };

                // Update input from Model
                var updateElementFromModel = function(newValue) {
                    $element.trigger('change');
                };

                // Observe: Element changes affect Model
                $element.on('change', function() {
                    updateModelFromElement();
                });

                $scope.$watch(function() {
                    return ngModel.$viewValue;
                }, function(newValue) {
                    updateElementFromModel(newValue);
                }, true);

                // Initialise BootstrapToggle
                $element.bootstrapToggle();
            }
        };
    });
