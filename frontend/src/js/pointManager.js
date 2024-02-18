define([
  'knockout',
  'appConstants'
],
  function (ko, AppConstants) {
    'use strict';
    class PointManager {

      constructor() {

        this.points = ko.observableArray([]);

        this.init = () => {
          return $.ajax({
            url: AppConstants.LAST_POINTS_URL,
          }).done((response) => {
            if (response.points.length > 0) {
              this.points.push(...response.points);
            }
          });
        };

      }
    }

    return new PointManager();

  }
);
