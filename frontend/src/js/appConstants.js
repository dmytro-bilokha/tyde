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
        this.addConstant('CONTEXT_PATH', '/tyde');
        this.addConstant('LOGOUT_URL', `${this.CONTEXT_PATH}/logout`);
        this.addConstant('POINT_URL', `${this.CONTEXT_PATH}/service/point`);
        this.addConstant('WEB_SOCKET_URL', `${this.CONTEXT_PATH}/point-endpoint`);
				this.addConstant('USER_URL', `${this.CONTEXT_PATH}/service/user`);
        this.addConstant('AUTH_URL', `${this.CONTEXT_PATH}/service/auth`);
      }

    }

    return new AppConstants();
  });
