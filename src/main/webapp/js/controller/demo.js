var app = angular.module('demoApp', []);

app.controller('demoController', function ($scope, websocketService) {

    $scope.regionMap = new Map();
    $scope.subRegions;
    $scope.currentRegion;
    $scope.parentRegion;
	
	websocketService.onmessage(

                function (evt) {

                    console.log('The response was:'+evt.data);

                    if (evt.data!="DONE") {//response of subscribe/unsubscribe event

                        var response = JSON.parse(evt.data);
                        var regionName = response.regionName;
                        var result = {
                            candidate1: response.candidate1,
                            candidate2: response.candidate2,
                            progress1: Math.round(parseInt(response.candidate1)/
                                (parseInt(response.candidate1)+parseInt(response.candidate2))*100)+'%',
                            progress2: Math.round(parseInt(response.candidate2)/
                                (parseInt(response.candidate1)+parseInt(response.candidate2))*100)+'%'
                        };

                        console.log('data received:'+response);
                        $scope.$apply(function () {
                            $scope.regionMap.set(regionName,result);
                            if (response.children.length>0) {
                                $scope.subRegions = response.children;
                                $scope.currentRegion = regionName;
                                $scope.parentRegion = response.parentRegion;
                            }

                        });
                    }

                 }
    );

    $scope.changeRegion = function(regionName) {

//        $scope.previousRegion = $scope.currentRegion;

        if ($scope.currentRegion != undefined) {

           var unSubscribeMessage = {
                           type:"unsubscribe",
                           region:$scope.currentRegion
           };
           websocketService.send(unSubscribeMessage);

        }

        var message = {
                type:"subscribe",
                region:regionName
        };
        websocketService.send(message);

    }

    $scope.changeRegion("Country");

});

app.factory('websocketService', [function() {
//    var onmessageDefer;
    var socket = {
        ws: new WebSocket("wss://127.0.0.1:8443/demo/graphWebSocket"),
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


