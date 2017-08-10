var app = angular.module('demoApp', []);

app.controller('demoController', function ($scope, websocketService) {

    $scope.regionMap = new Map();
    $scope.subRegions;
    $scope.currentRegion;
	
	websocketService.onmessage(

                function (evt) {

                    if (evt.data!="DONE") {//response of subscribe/unsubscribe event

                        var response = JSON.parse(evt.data);
                        var regionName = response.regionName;
                        var result = {
                            candidate1: response.candidate1,
                            candidate2: response.candidate2
                        };

                        console.log('data received:'+response);
                        $scope.$apply(function () {
                            $scope.regionMap.set(regionName,result);
                            if (response.children.length>0) {
                                $scope.subRegions = response.children;
                                $scope.currentRegion = regionName;
                            }

                        });
                    }

                 }
    );

    $scope.changeRegion = function(regionName) {


//        if ($scope.currentRegion != undefined) {
//
//           var unSubscribeMessage = {
//                           type:"unsubscribe",
//                           region:$scope.currentRegion
//           };
//           websocketService.send(unSubscribeMessage);
//
//        }

        var message = {
                type:"subscribe",
                region:regionName
        };
        websocketService.send(message);

    }

    console.log('Starting angular app...');
    $scope.changeRegion("Country");

});

app.factory('websocketService', [function() {
    console.log('Starting angular app factory...');
//    var onmessageDefer;
    var socket = {
        ws: new WebSocket("ws://127.0.0.1:8080/demo/graphWebSocket"),
        send: function(data) {
            data = JSON.stringify(data);
            if (socket.ws.readyState == 1) {
                socket.ws.send(data);
            }
        },
        onmessage: function(callback) {

            socket.ws.onmessage = callback;

//            if (socket.ws.readyState == 1) {
//                socket.ws.onmessage = callback;
//            } else {
//                onmessageDefer = callback;
//            }
        }
    };
    socket.ws.onopen = function(event) {

    };
    return socket;
}]);


