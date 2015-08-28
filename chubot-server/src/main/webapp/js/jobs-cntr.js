myApp.controller("JobsController",  function ($scope, $http, $filter) {

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
});

//called the Angular object to create a module named myApp
var pagingApp = angular.module("pagingApp", ['tm.pagination'], jsonEnAdaption);

//The $scope is what lets us bind data to elements in the UI.
//AngularJS $http 是一个用于读取web服务器上数据的服务。
pagingApp.controller("JobsController", ['$scope', 'JobsService', function ($scope, JobsService) {
    //配置分页基本参数
    $scope.paginationConf = {
        currentPage: 1,
        itemsPerPage: 5,
    };

    var pageHistory = function () {
        var postData = {
            pageIndex: $scope.paginationConf.currentPage,
            pageSize: $scope.paginationConf.itemsPerPage
        };
        JobsService.list(postData).success(function (data) {
            $scope.paginationConf.totalItems = data.total;
            $scope.items = data.items;
        });
    };

    $scope.refreshHistory = function(){
        //配置分页基本参数
        $scope.paginationConf.currentPage=1;
        $scope.paginationConf.forceRefresh = ! $scope.paginationConf.forceRefresh;
    };

    /***************************************************************
     当页码和页面记录数发生变化时监控后台查询
     如果把currentPage和itemsPerPage分开监控的话则会触发两次后台事件。
     ***************************************************************/
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage + paginationConf.forceRefresh', pageHistory);
}]);

//业务类
pagingApp.factory('JobsService', ['$http', function ($http) {
    var list = function (postData) {
        return $http.post('/api/spider/job/history', postData);
    }
    return {
        list: function (postData) {
            return list(postData);
        }
    }
}]);
