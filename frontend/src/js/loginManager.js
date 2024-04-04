define([
  'knockout',
  'appConstants'
],
  function (ko, appConstants) {
    'use strict';

    class LoginManager {

      constructor() {
        this.userLogin = ko.observable('');

        this.login = (login, password, rememberMe) => {
          return $.ajax({
            url: `${appConstants.AUTH_URL}/login`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ login: login, password: password, rememberMe: rememberMe })
          }).done(() => {
            this.userLogin(login);
          }).fail(() => {
            this.userLogin('');
          });
        };

        this.logout = () => {
          return $.ajax({
            url: `${appConstants.AUTH_URL}/logout`,
            type: 'POST'
          }).done(() => {
            this.userLogin('');
            window.location.replace(appConstants.LOGOUT_URL);
          });
        };

        this.init = () => {
          $.ajax({
            url: appConstants.USER_URL,
          }).done((data) => {
            this.userLogin(data.login);
          }).fail(() => {
            this.userLogin('');
          });
        };

        this.registerFailedAuthorization = () => {
          this.userLogin('');
        };

      }

    }

    return new LoginManager();
  });