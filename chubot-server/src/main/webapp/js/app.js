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

angular.module('myApp', ['ngCookies','ngRoute','chu.toggle','tm.pagination'], jsonEnAdaption);
angular.module('myApp').directive("limitTo", [function() {
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
}).directive('alertPrompt', function(){
    return {
        restrict: 'E',
        replace: true,
        templateUrl: '/view/template/alert-prompt.html',
        scope: {
            para: '='
        },
        link: function(scope){

        }
    };
}).config(function($routeProvider, $controllerProvider, $locationProvider, $compileProvider, $filterProvider, $provide){
    $locationProvider.html5Mode(true).hashPrefix('!');
    angular.module('myApp').registerCtrl = $controllerProvider.register;
    angular.module('myApp').registerDirective = $compileProvider.directive;
    angular.module('myApp').registerRoute = $routeProvider.when;
    angular.module('myApp').registerFilter = $filterProvider.register;
    $routeProvider
        .when('/jobs', {
            templateUrl: '/view/jobs/commit.html'
        }).when('/admin',{
            templateUrl:'/view/admin/jars.html'
        }).when('/agents',{
            templateUrl:'/view/agents/current.html'
        }).when('/agents/agent',{
            templateUrl:'/view/agents/agent.html'
        }).when('/user/login',{
            controller: 'LoginController',
            templateUrl:'/view/user/login.html'
        }).when('/user/register',{
            templateUrl:'/view/user/register.html'
        }).when('/help',{
            templateUrl:'/view/help/help.html'
        }).otherwise({
            redirectTo: '/jobs'
        });
}).controller("NavController", function ($scope, $rootScope) {

}).controller("UserStateController", function ($scope, $http, $window) {
    $scope.logout = function(){
        $http.post('/user/doLogout',{})
            .success(function(data){
                if(data.error == undefined){
                    $window.location.href = '/user/login';
                }else {
                    alert(data.error);
                }
            }).error(function(data){
            });
    }
}).run(function($rootScope){
        $rootScope.$on('$viewContentLoaded', function(event){
            if($rootScope.myNavShow == undefined){
                $rootScope.myNavShow = true;
            }
        });
    }
).factory('rememberMe', function($cookies) {
        function fetchValue(name) {
            console.log('name: ' + $cookies.get(name));
            return $cookies.get(name);
        }
        return function(name, values) {
            if(arguments.length === 1) return fetchValue(name);
            if(values == ''){
                $cookies.remove(name);
                return;
            }
            var option = {};
            var value = values;
            if(typeof values === 'object') {
                value = (typeof values.value === 'object') ? angular.toJson(values.value) : values.value;
                if(values.expires) {
                    var date = new Date();
                    date.setTime( date.getTime() + (values.expires * 24 *60 * 60 * 1000));
                    option.expires = date.toGMTString();
                }
                if(values.path){
                    option.path = values.path;
                }
                if(values.secure){
                    option.secure = true;
                }
            }
            $cookies.put(name, value, option);
        }
    })
;
var AlertType = {INFO: 0, Success: 1, Warn: 2, Danger: 3};