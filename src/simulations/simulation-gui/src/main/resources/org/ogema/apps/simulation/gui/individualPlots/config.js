// == Configuration
// config.js is where you will find the core Grafana configuration. This file contains parameter that
// must be set before Grafana is run for the first time.
define(['settings'], function(Settings) {
  var SERVLET_ADDRESS="/de/iwes/simulations/simgui/servlet/individual/fake_influxdb";
  return new Settings({

      /* Data sources
      * ========================================================
      * Datasources are used to fetch metrics, annotations, and serve as dashboard storage
      *  - You can have multiple of the same type.
      *  - grafanaDB: true    marks it for use for dashboard storage
      *  - default: true      marks the datasource as the default metric source (if you have multiple)
      *  - basic authentication: use url syntax http://username:password@domain:port
      */

      // InfluxDB example setup (the InfluxDB databases specified need to exist)
      
      datasources: {
        influxdb: {
          type: 'influxdb',
     //     url: "http://my_influxdb_server:8086/db/database_name",
          url: SERVLET_ADDRESS,
          username: 'admin',
          password: 'admin',
          default: true,
        }
      },

      /* Global configuration options
      * ========================================================
      */

      // specify the limit for dashboard search results
      search: {
        max_results: 100
      },

      // default home dashboard
     // default_route: '/dashboard/file/default.json',
      default_route: '/dashboard/script/scripted_async.js',

      // set to false to disable unsaved changes warning
      unsaved_changes_warning: false,

      // set the default timespan for the playlist feature
      // Example: "1m", "1h"
      playlist_timespan: "1m",

      // If you want to specify password before saving, please specify it below
      // The purpose of this password is not security, but to stop some users from accidentally changing dashboards
      admin: {
        password: ''
      },

      // Change window title prefix from 'Grafana - <dashboard title>'
      window_title_prefix: 'Grafana - ',

      // Add your own custom panels
      plugins: {
        // list of plugin panels
        panels: [],
        // requirejs modules in plugins folder that should be loaded
        // for example custom datasources
        dependencies: [],
      }
      
      
    });
  console.log("Instantiated settings: ",this);
});



