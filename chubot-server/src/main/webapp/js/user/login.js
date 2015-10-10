angular.module("myApp").registerCtrl("LoginController", function ($scope, $rootScope, $http, $window) {
    $rootScope.myNavShow = false;
    $scope.alertPara = {
        isShowAlert: false,
        alertInfo: '',
        status: "alert-danger"
    };
    $scope.loginUser=function(){
        var pwd = $scope.login.password;
        pwd = hex_sha1(pwd);
        pwd = hex_md5(pwd);
        var myData = {user: $scope.login.user, password: pwd, captcha: $scope.captchaVal };
        console.log(myData);
        $http.post('/user/doLogin', myData)
            .success(function(data){
                if(data.error == undefined){
                    console.log("redirect home page");
                    $window.location.assign("/");
                }else {
                    $scope.login.captchaVal='';
                    $scope.reloadCaptcha();
                    $scope.alertMsg(data.error);
                }
            }).error(function(data){
            });
    };
    $scope.$watch("login.user + login.password", function(newValue){
        $scope.alertPara.isShowAlert = false;
    });
    $scope.alertMsg = function(msg){
        $scope.alertPara.alertInfo = msg;
        $scope.alertPara.isShowAlert = true;
    };
});