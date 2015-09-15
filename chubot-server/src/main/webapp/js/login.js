myApp.controller("LoginController", function ($scope, $http) {
    $scope.login = {};
    $scope.isShowAlert = false;
    $scope.status = "alert-danger";
    $scope.loginUser=function(){
        var myData = {email: $scope.login.email, password: $scope.login.password};
        $http.post('/user/login', myData)
            .success(function(data, status, headers, config){
                if(data.error == undefined){
                }else {
                    $scope.alertMsg(data.error);
                }
            }).error(function(data,status,headers,config){
            });
    };
    $scope.$watch("login.email + login.password", function(newValue){
        $scope.isShowAlert = false;
    });
    $scope.alertMsg = function(msg){
        $scope.isShowAlert = true;
        $scope.alertInfo =msg;
    };
});