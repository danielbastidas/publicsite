var app = angular.module('transmitApp', []);

app.controller('transmitController', function ($scope, $http) {

    $scope.tally="";

    $scope.transmit = function() {

        return $http({
                    url : '/demo/rest/transmitter/transmit',
                    method : 'GET',
                    params : {}
                }).success(function (data) {
                          	$scope.tally = data.data;
                          }).error(function (data) {
                            $scope.tally="There was an error inserting the tally";
                          }).finally(function () {
                            $scope.tally="There was an error inserting the tally";
                          });

    };

});