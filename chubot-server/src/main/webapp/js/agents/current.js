angular.module('myApp').registerCtrl("AgentsController", function ($scope, $http, $filter) {
    $scope.refreshAgents=function(){
        $http.get('/agents/agents').success(function(data, status, headers, config) {
            $scope.items = data.items;
        });
    };
    $scope.refreshAgents();
});