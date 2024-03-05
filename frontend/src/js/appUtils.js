define([],
  function() {
    'use strict';

		class AppUtils {

			constructor() {

				this.isStringNotBlank = (text) =>  {
					return text !== undefined && text !== null && text.trim().length > 0;
				};

				this.isStringBlank = (text) => {
					return !this.isStringNotBlank(text);
				};

			}

		}

    return new AppUtils();
  });
