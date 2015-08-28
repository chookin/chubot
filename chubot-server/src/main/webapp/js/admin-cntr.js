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