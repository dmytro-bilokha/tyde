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
            // request ten last points
            this.ws.send("10");
          };
          this.ws.onclose = () => {
            console.log("Websocket connection closed");
          };
          this.ws.onmessage = (evt) => {
            console.log("Got websocket message: " + evt.data);
            const payload = JSON.parse(evt.data);
            if (payload.points.length > 0) {
              this.points.push(...payload.points);
            }
          };
        };

      }

    }

    return new PointManager();

  }
);
