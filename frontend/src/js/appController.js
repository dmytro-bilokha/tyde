define([
  'knockout',
  'loginManager',
  'notificationManager',
  'ojs/ojcontext',
  'ojs/ojmodule-element-utils',
  'ojs/ojresponsiveutils',
  'ojs/ojresponsiveknockoututils',
  'ojs/ojcorerouter',
  'ojs/ojmodulerouter-adapter',
  'ojs/ojknockoutrouteradapter',
  'ojs/ojurlparamadapter',
  'ojs/ojarraydataprovider',
  'ojs/ojknockouttemplateutils',
  'ojs/ojmodule-element',
  'ojs/ojknockout',
  'oj-c/message-toast'
],
  function(
    ko,
    loginManager,
    notificationManager,
    Context,
    ModuleElementUtils,
    ResponsiveUtils,
    ResponsiveKnockoutUtils,
    CoreRouter,
    ModuleRouterAdapter,
    KnockoutRouterAdapter,
    UrlParamAdapter,
    ArrayDataProvider,
    KnockoutTemplateUtils) {

     function ControllerViewModel() {

        this.KnockoutTemplateUtils = KnockoutTemplateUtils;

        // Handle announcements sent when pages change, for Accessibility.
        this.manner = ko.observable('polite');
        this.message = ko.observable();

        this.userLogin = loginManager.userLogin;

        announcementHandler = (event) => {
          this.message(event.detail.message);
          this.manner(event.detail.manner);
      };

      document.getElementById('globalBody').addEventListener('announce', announcementHandler, false);

      // Media queries for repsonsive layouts
      const smQuery = ResponsiveUtils.getFrameworkQuery(ResponsiveUtils.FRAMEWORK_QUERY_KEY.SM_ONLY);
      this.smScreen = ResponsiveKnockoutUtils.createMediaQueryObservable(smQuery);

      this.closeMessage = (event) => {
        notificationManager.deleteNotification(event.detail.key);
      };

      this.userMenuAction = (event) => {
        if (event.detail.selectedValue === 'logout') {
          notificationManager.removeAllNotificationsOfType('login');
          loginManager
            .logout()
            .fail((jqXHR, textStatus, errorThrown) => {
              notificationManager.addNotification({
                severity: 'error',
                summary: 'Logout failed',
                detail: `${textStatus} - ${errorThrown}`,
                type: 'login'
              });
            });
          return;
        }

        console.log(event);
      };

      this.ModuleElementUtils = ModuleElementUtils;
      this.messagesProvider = new ArrayDataProvider(notificationManager.notifications, {keyAttributes: 'id'});
     }

     loginManager.init();
     // release the application bootstrap busy state
     Context.getPageContext().getBusyContext().applicationBootstrapComplete();

     return new ControllerViewModel();
  }
);
