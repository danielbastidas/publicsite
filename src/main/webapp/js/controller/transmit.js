var app = angular.module('transmitApp', []);

app.controller('transmitController', function ($scope, $http) {

    $scope.tally="";

    $scope.transmit = function() {

        return $http({
                    url : '/demo/rest/transmitter/transmit',
                    method : 'GET',
                    params : {}
                }).success(function (data) {
                            console.log('The response was:'+data.message);
                          	$scope.tally = data.message;
                          }).error(function (data) {
                            console.log('The response was:'+data);
                            $scope.tally="There was an error inserting the tally";
                          }).finally(function () {
//                            $scope.tally="There was an error inserting the tally";
                          });

    };

});