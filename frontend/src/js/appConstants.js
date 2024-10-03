define([],
  function() {
    'use strict';

    class AppConstants {

      addConstant(name, value) {
        Object.defineProperty(this, name, {
          value: value,
          enumerable: true
        });
      }

      // TODO: remove unused constants
      constructor() {
        this.addConstant('POINTS_LIMIT', 20);
        this.addConstant('POINTS_UPDATE_INTERVAL_MS', 30000);
        this.addConstant('CONTEXT_PATH', '/tyde');
        this.addConstant('LOGOUT_URL', `${this.CONTEXT_PATH}/logout`);
        this.addConstant('GPS_DEVICE_URL', `${this.CONTEXT_PATH}/service/gps-device`);
				this.addConstant('USER_URL', `${this.CONTEXT_PATH}/service/user`);
        this.addConstant('AUTH_URL', `${this.CONTEXT_PATH}/service/auth`);
      }

    }

    return new AppConstants();
  });
