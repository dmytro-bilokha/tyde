define([
  'knockout'
],
  function (ko) {
    'use strict';

    class NotificationManager {

      constructor() {
        this.notifications = ko.observableArray([]);

        this.addNotification = (notification) => {
          this.notifications.push(notification);
        };

        this.removeAllNotificationsOfType = (notificationType) => {
          this.notifications.remove(item => item.type === notificationType);
        };

      }

    }

    return new NotificationManager();
  });
