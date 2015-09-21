angular.module("myApp").registerCtrl("JobController",  function ($scope, $rootScope, $http, $filter) {
    $rootScope.navShow = true;
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