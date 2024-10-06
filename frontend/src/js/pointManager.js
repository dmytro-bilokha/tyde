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
        this.retryCount = 0;

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

        this.connect = (deviceId, timePeriod) => {
          this.disconnect();
          this.gpsDeviceId = parseInt(deviceId);
          if (isNaN(this.gpsDeviceId)) {
            // If value is not a valid int, we don't do anything
            return;
          }
          this.timePeriod = timePeriod;
          this.fetchLastPoints();
        };

        this.fetchLastPoints = () => {
          if ("fetchLastPointsTimeoutId" in this) {
            clearTimeout(this.fetchLastPointsTimeoutId);
          }
          if (!isNaN(this.timePeriod)) {
            const now = new Date();
            this.points.remove(
              (point) => point.timestamp + this.timePeriod * 60 * 1000 < now
            );
          }
          const existingPointsArray = this.points();
          const lastPointId = existingPointsArray.length > 0
            ? existingPointsArray[existingPointsArray.length - 1].id
            : 0;
          return $.ajax({
            url: `${AppConstants.GPS_DEVICE_URL}/${this.gpsDeviceId}/point?lastPointId=${lastPointId}&limit=${AppConstants.POINTS_LIMIT}`,
          }).done((response) => {
            const newPoints = response.points
              .map(payloadPoint => ({
                id: payloadPoint.id,
                lat: payloadPoint.lat,
                lon: payloadPoint.lon,
                timestamp: Date.parse(payloadPoint.timestamp),
                speed: payloadPoint.speed,
                altitude: payloadPoint.altitude,
                direction: payloadPoint.direction,
                accuracy: payloadPoint.accuracy
              }))
              .sort((aPoint, bPoint) => aPoint.id - bPoint.id);
            for (const newPoint of newPoints) {
              if (newPoint.id > lastPointId) {
                this.points.push(newPoint);
                if (this.points().length > AppConstants.POINTS_LIMIT) {
                  this.points.splice(0, 1);
                }
              }
            }
            this.fetchLastPointsTimeoutId = setTimeout(
              this.fetchLastPoints, AppConstants.POINTS_UPDATE_INTERVAL_MS);
          }).fail((jqXHR, textStatus, errorThrown) => {
            let serverMessage = null;
            if (jqXHR.responseJSON) {
              serverMessage = jqXHR.responseJSON.message;
            }
            notificationManager.addNotification({
              severity: 'error',
              summary: 'Failed to get GPS points update',
              detail: serverMessage ? serverMessage : `${textStatus} - ${errorThrown}`,
              type: 'point'
            });
          });
        };

        this.init = (onPointsChange) => {
          this.points.subscribe(onPointsChange, null, "arrayChange");
        };

        this.destruct = () => {
          this.disconnect();
        }

        this.disconnect = () => {
          if ("fetchLastPointsTimeoutId" in this) {
            clearTimeout(this.fetchLastPointsTimeoutId);
          }
          while (this.points().length > 0) {
            this.points.pop();
          }
        };
      }

    }

    return new PointManager();

  }
);
