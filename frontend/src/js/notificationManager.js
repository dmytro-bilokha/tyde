define([
  'knockout'
],
  function (ko) {
    'use strict';

    class NotificationManager {

      constructor() {
        this.currentId = 1;
        this.notifications = ko.observableArray([]);

        this.addNotification = (notification) => {
          this.notifications.push({
            id: "notification" + this.currentId++,
            severity: notification.severity,
            summary: notification.summary,
            detail: notification.detail,
            type: notification.type
          });
        };

        this.deleteNotification = (notificationId) => {
          this.notifications.remove(item => item.id === notificationId);
        };

        this.removeAllNotificationsOfType = (notificationType) => {
          this.notifications.remove(item => item.type === notificationType);
        };

      }

    }

    return new NotificationManager();
  });
