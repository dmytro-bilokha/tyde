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

      constructor() {
        this.addConstant('POINTS_LIMIT', 20);
        this.addConstant('PING_INTERVAL_MS', 30000);
        this.addConstant('PING_TIMEOUT_MS', 15000);
        this.addConstant('PING_MESSAGE', 'PING');
        this.addConstant('CONTEXT_PATH', '/tyde');
        this.addConstant('LOGOUT_URL', `${this.CONTEXT_PATH}/logout`);
        this.addConstant('POINT_URL', `${this.CONTEXT_PATH}/service/point`);
        this.addConstant('GPS_DEVICE_URL', `${this.CONTEXT_PATH}/service/gps-device`);
        this.addConstant('WEB_SOCKET_URL', `${this.CONTEXT_PATH}/point-endpoint`);
				this.addConstant('USER_URL', `${this.CONTEXT_PATH}/service/user`);
        this.addConstant('AUTH_URL', `${this.CONTEXT_PATH}/service/auth`);
      }

    }

    return new AppConstants();
  });
