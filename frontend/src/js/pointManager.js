define([
  'knockout',
  'appConstants'
],
  function (ko, AppConstants) {
    'use strict';
    class PointManager {

      constructor() {

        this.points = ko.observableArray([]);

        this.init = (onPointsChange) => {
          this.points.subscribe(onPointsChange, null, "arrayChange");
          const webSocketUrl = new URL(AppConstants.WEB_SOCKET_URL, location.href);
          webSocketUrl.protocol = webSocketUrl.protocol.replace('http', 'ws');
          this.ws = new WebSocket(webSocketUrl);
          this.ws.onopen = () => {
            console.log("Websocket connection openned");
            this.ws.send(AppConstants.POINTS_LIMIT);
          };
          this.ws.onclose = () => {
            console.log("Websocket connection closed");
          };
          this.ws.onmessage = (evt) => {
            const payload = JSON.parse(evt.data);
            const newPoints = payload.points
              .map(payloadPoint => ({
                lat: payloadPoint.lat,
                lon: payloadPoint.lon,
                timestamp: Date.parse(payloadPoint.timestamp),
                speed: payloadPoint.speed,
                altitude: payloadPoint.altitude,
                direction: payloadPoint.direction,
                accuracy: payloadPoint.accuracy
              }))
              .sort((aPoint, bPoint) => aPoint.timestamp - bPoint.timestamp);
            const existingPointsArray = this.points();
            let lastTimestamp = existingPointsArray.length > 0
              ? existingPointsArray[existingPointsArray.length - 1].timestamp
              : 0;
            for (const newPoint of newPoints) {
              if (newPoint.timestamp > lastTimestamp) {
                this.points.push(newPoint);
                if (this.points().length > AppConstants.POINTS_LIMIT) {
                  this.points.splice(0, 1);
                }
              }
            }
          };
        };

        this.destruct = () => {
          this.points.removeAll();
          if ("ws" in this) {
            this.ws.close();
          }
        }
      }

    }

    return new PointManager();

  }
);
