angular.module("myApp").controller("JobHistoryController", ['$scope', 'JobsService', function ($scope, JobsService) {
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
}]).factory('JobsService', ['$http', function ($http) {
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