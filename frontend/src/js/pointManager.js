define([
  'knockout',
  'appConstants',
  'notificationManager'
],
  function (ko, AppConstants, notificationManager) {
    'use strict';
    class PointManager {

      constructor() {

        this.points = ko.observableArray([]);

        this.fetchAvailableDevices = (devicesObservableArray) => {
          return $.ajax({
            url: AppConstants.GPS_DEVICE_URL,
          }).done((response) => {
            devicesObservableArray.removeAll();
            response.availableDevices.forEach((device) => {
              devicesObservableArray.push({
                value: device.id,
                label: device.description
              });
            });
          }).fail((jqXHR, textStatus, errorThrown) => {
            let serverMessage = null;
            if (jqXHR.responseJSON) {
              serverMessage = jqXHR.responseJSON.message;
            }
            notificationManager.addNotification({
              severity: 'error',
              summary: 'Failed to get list of GPS devices',
              detail: serverMessage ? serverMessage : `${textStatus} - ${errorThrown}`,
              type: 'gps-device'
            });
          });
        };

        this.connect = (deviceId) => {
          this.disconnect();
          this.gpsDeviceId = parseInt(deviceId);
          if (isNaN(this.gpsDeviceId)) {
            // If value is not a valid int, we only disconnect and don't do anything
            return;
          }
          const webSocketUrl = new URL(AppConstants.WEB_SOCKET_URL, location.href);
          webSocketUrl.protocol = webSocketUrl.protocol.replace('http', 'ws');
          this.ws = new WebSocket(webSocketUrl);

          this.onPingTimeout = () => {
            console.error("Websocket ping timed out, closing the connection");
            this.ws.close();
          };

          this.sendPing = () => {
            this.ws.send(AppConstants.PING_MESSAGE);
            this.pingTimeoutId = setTimeout(this.onPingTimeout, AppConstants.PING_TIMEOUT_MS);
          };

          this.acceptPong = () => {
            if ("pingTimeoutId" in this) {
              clearTimeout(this.pingTimeoutId);
            }
          };

          this.ws.onopen = () => {
            console.log("Websocket connection openned");
            this.ws.send(`${this.gpsDeviceId}:${AppConstants.POINTS_LIMIT}`);
            this.pingIntervalId = setInterval(this.sendPing, AppConstants.PING_INTERVAL_MS);
          };

          this.ws.onclose = () => {
            if ("pingIntervalId" in this) {
              clearInterval(this.pingIntervalId);
            }
            if (this.closeRequested) {
              console.log("Websocket connection closed, as requested");
              this.closeRequested = false;
            } else {
              console.error("Websocket connection forcibly closed");
              notificationManager.removeAllNotificationsOfType('pointConnection');
              notificationManager.addNotification({
                severity: 'error',
                summary: 'Server connection error',
                detail: 'Connection to the server disrupted, location is not updated',
                type: 'pointConnection'
              });
            }
          };

          this.ws.onerror = (err) => {
            console.error("Websocket connection error: ", err.message);
            this.ws.close();
          };

          this.ws.onmessage = (evt) => {
            this.acceptPong();
            if (this.closeRequested) {
              // Don't accept new points data if we are closing
              return;
            }
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

        this.init = (onPointsChange) => {
          this.points.subscribe(onPointsChange, null, "arrayChange");
        };

        this.destruct = () => {
          this.disconnect();
        }

        this.disconnect = () => {
          this.closeRequested = true;
          while (this.points().length > 0) {
            this.points.pop();
          }
          if ("ws" in this) {
            this.ws.close();
          } else {
            this.closeRequested = false;
          }
        };
      }

    }

    return new PointManager();

  }
);
