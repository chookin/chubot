'use strict';
var jsonEnAdaption = function($httpProvider) {
    // http://victorblog.com/2012/12/20/make-angularjs-http-service-behave-like-jquery-ajax/
    // Make AngularJS $http service behave like jQuery.ajax()

    // Use x-www-form-urlencoded Content-Type
    $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';

    /**
     * The workhorse; converts an object to x-www-form-urlencoded serialization.
     * @param {Object} obj
     * @return {String}
     */
    var param = function(obj) {
        var query = '', name, value, fullSubName, subName, subValue, innerObj, i;

        for(name in obj) {
            value = obj[name];

            if(value instanceof Array) {
                for(i=0; i<value.length; ++i) {
                    subValue = value[i];
                    fullSubName = name + '[' + i + ']';
                    innerObj = {};
                    innerObj[fullSubName] = subValue;
                    query += param(innerObj) + '&';
                }
            }
            else if(value instanceof Object) {
                for(subName in value) {
                    subValue = value[subName];
                    fullSubName = name + '[' + subName + ']';
                    innerObj = {};
                    innerObj[fullSubName] = subValue;
                    query += param(innerObj) + '&';
                }
            }
            else if(value !== undefined && value !== null)
                query += encodeURIComponent(name) + '=' + encodeURIComponent(value) + '&';
        }

        return query.length ? query.substr(0, query.length - 1) : query;
    };

    // Override $http service's default transformRequest
    $httpProvider.defaults.transformRequest = [function(data) {
        return angular.isObject(data) && String(data) !== '[object File]' ? param(data) : data;
    }];
};

var myApp = angular.module("myApp", [], jsonEnAdaption);
myApp.directive("limitTo", [function() {
    return {
        require: 'ngModel',
        restrict: "A",
        scope: {
            ngModel: '='
        },
        link: function(scope, elem, attrs, ngModel) {
            var limit = attrs.limitTo;
            scope.$watch(function () {
                return ngModel.$modelValue;
            }, function(newValue, oldValue){
                if(newValue == undefined){
                    return;
                }
                if(newValue.length > limit){
                    scope.ngModel = newValue.substring(0, limit);
                }
            }, true);
        }
    }
}]).directive('captcha', function() {
    return {
        restrict: 'EA',
        replace: true,
        template: '<img class="captcha" ng-src="/captcha?width=145&height=36&fontsize=22&time={{time}}" ng-click="reloadCaptcha()" >',
        link: function(scope, element, attrs, controller) {
            scope.time = new Date().getTime();
            scope.reloadCaptcha = function() {
                console.log("load captcha");
                return scope.time = new Date().getTime();
            };
        }
    };
});
var AlertType = {INFO: 0, Success: 1, Warn: 2, Danger: 3};