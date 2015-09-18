var toogleApp = angular.module("toogleApp", ['chu.toggle'], jsonEnAdaption);
toogleApp.controller("JobsController",  function ($scope, $http, $filter) {
    $scope.newJob = '';
    $scope.singleton=false;
    $scope.status="alert-info";
    $scope.isShowAlert = false;
    $scope.chuToggle = function(){
        console.log("singleton: " + $scope.singleton);
    };
    $scope.commitJob=function($event, newJob, singleton){
        if(!confirm('Commit job: '+ "'" + newJob +"', singleton:"+ singleton + "?")){
            return;
        }
        var obj;
        try{
            obj = angular.fromJson(newJob);
        }catch(e){
            $scope.alertMsg(AlertType.Warn, "Fail to parse json object from job string '" + newJob + "'.");
            return;
        }
        obj.singleton = singleton;
        var myData = {data: angular.toJson(obj)};
        $http.post('/jobs/commitJob',myData)
            .success(function(data, status, headers, config){
                if(data.error == undefined){
                    $scope.alertMsg(AlertType.Success, "Success to commit job.");
                }else {
                    $scope.alertMsg(AlertType.Warn, "Fail to commit job, "+data.error);
                }
            }).error(function(data,status,headers,config){;
                $scope.alertMsg(AlertType.Warn, "Fail to commit job, "+data.error);
            });
    };
    $scope.$watch("newJob", function(newValue){
        $scope.isShowAlert = false;
    });
    $scope.alertMsg = function(type, msg){
        switch (type){
            case AlertType.Success:
                $scope.status = "alert-success";
                break;
            case AlertType.Warn:
                $scope.status = "alert-danger";
                break;
        }
        $scope.isShowAlert = true;
        $scope.alertInfo =msg;
    };
});

var pagingApp = angular.module("pagingApp", ['tm.pagination'], jsonEnAdaption);
pagingApp.controller("JobsController", ['$scope', 'JobsService', function ($scope, JobsService) {
    //配置分页基本参数
    $scope.paginationConf = {
        currentPage: 1,
        itemsPerPage: 5
    };

    var pageHistory = function () {
        var paras = [
            {name:'pageIndex', val:$scope.paginationConf.currentPage},
            {name:'pageSize', val:$scope.paginationConf.itemsPerPage}
        ];
        JobsService.list(paras).success(function (data) {
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
    var list = function (paras) {
        var url = '/jobs/history';
        if(paras.length > 0){
            url = url + '?';
            for(var o in paras){
                var para = paras[o];
                url = url + para.name + '=' + para.val + '&';
            }
            url = url.substr(0, url.length - 1);
        }
        console.log("get history: "+url);
        return $http.get(url);
    };
    return {
        list: function (postData) {
            return list(postData);
        }
    }
}]);
