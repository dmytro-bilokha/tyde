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
                value: parseInt(device.id),
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
          this.gpsDeviceId = deviceId;
          this.timePeriod = timePeriod;
          if (isNaN(this.gpsDeviceId) || isNaN(this.timePeriod)) {
            // If GPS device or period not provided, no point to fetch
            return;
          }
          this.fetchLastPoints();
        };

        this.fetchLastPoints = () => {
          if ("fetchLastPointsTimeoutId" in this) {
            clearTimeout(this.fetchLastPointsTimeoutId);
          }
          const now = new Date();
          const earliestAllowed = now - this.timePeriod * 60 * 1000;
          this.points.remove(
            (point) => point.timestamp < earliestAllowed
          );
          const existingPointsArray = this.points();
          const requestTimestamp = existingPointsArray.length > 0
            ? existingPointsArray[existingPointsArray.length - 1].timestamp + 1 // add 1 ms to prevent getting the same point multiple times
            : earliestAllowed;
          return $.ajax({
            url: `${AppConstants.GPS_DEVICE_URL}/${this.gpsDeviceId}/point?fromTimestamp=${requestTimestamp}`,
          }).done((response) => {
            const newPoints = response.points
              .map(payloadPoint => ({
                id: payloadPoint.id,
                lat: payloadPoint.lat,
                lon: payloadPoint.lon,
                timestamp: payloadPoint.timestamp,
                speed: payloadPoint.speed,
                altitude: payloadPoint.altitude,
                direction: payloadPoint.direction,
                accuracy: payloadPoint.accuracy
              }))
              .sort((aPoint, bPoint) => aPoint.timestamp - bPoint.timestamp);
            for (const newPoint of newPoints) {
              this.points.push(newPoint);
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
            this.points.shift();
          }
        };
      }

    }

    return new PointManager();

  }
);
