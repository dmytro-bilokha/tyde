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
        this.addConstant('POINT_URL', `${this.CONTEXT_PATH}/service/point`);
        this.addConstant('LAST_POINTS_URL', `${this.POINT_URL}?quantity=10`);
      }

    }

    return new AppConstants();
  });
