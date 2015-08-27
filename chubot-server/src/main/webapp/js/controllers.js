'use strict';
//将"use strict"放在脚本文件的第一行，则整个脚本都将以"严格模式"运行。如果这行语句不在第一行，则无效，整个脚本以"正常模式"运行。

//called the Angular object to create a module named myApp
var myApp = angular.module("myApp", [],function($httpProvider) {
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
});

//The $scope is what lets us bind data to elements in the UI.
//AngularJS $http 是一个用于读取web服务器上数据的服务。
myApp.controller("JobsController", function ($scope, $http, $filter) {
    $scope.refreshHistory=function(){
        $http.get('/api/spider/job/history').success(function(data, status, headers, config) {
            $scope.items = data.items;
        });
    };

    $scope.commitJob=function($event, newJob){
        if(!confirm('Commit job: '+ "'" + newJob +"'?")){
            return;
        }
        var myData = {data: newJob};
        $http.post('/api/spider/job/commitJob',myData)
            .success(function(data, status, headers, config){
                alert("success to commit job");
            }).error(function(data,status,headers,config){
                alert("fail to commit job");
            });
    };

    $scope.refreshHistory();
});

//The $scope is what lets us bind data to elements in the UI.
//AngularJS $http 是一个用于读取web服务器上数据的服务。
myApp.controller("AgentsController", function ($scope, $http, $filter) {
    $scope.refreshAgents=function(){
        $http.get('/agents/agents').success(function(data, status, headers, config) {
            $scope.items = data.items;
        });
    };
    $scope.refreshAgents();
});

myApp.controller("AdminController", function($scope, $http, $filter){
    $scope.addJars=function($event, jars){
        var myData = {data: jars};
        $http.post('/admin/addJars', myData)
            .success(function(data, status, headers, config){
                alert("success to add jars");
            }).error(function(data,status,headers,config){
                alert("fail to add jars");
            });
    };
});