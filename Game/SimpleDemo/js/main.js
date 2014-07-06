/**
*    Dependencies
*       jQuery
*/

/**
 *  A simple Adaptilo browser client.
 *  
 *  Notice : 
 *
 *  import jQuery and this file in your html page.
 *  <script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
 *  <script src="js/main.js"></script>
 *   
 *  Create your own configuration or use Adaptilo.defaultConfiguration;
 *  var myConfiguration = Adaptilo.defaultConfiguration;
 *
 *  Instantiate a new platform.
 *  var myPlatform = Adaptilo.platform(myConfiguration);
 *
 *  Connect the platform to the server.
 *  myPlatform.connect();
 *
 */

var Adaptilo = Adaptilo || {};

Adaptilo.defaultConfiguration = (function() {
    "use strict";
    return {
        server_ip           :   "192.168.0.1",
        server_port         :   "8080",
        onSocketOpen        :   function(event) {
            console.log("[Web Socket] onSocketOpen -> " + event);
        },
        onSocketClose       :   function(event) {
            console.log("[Web Socket] onSocketClose -> " + event);
        },
        onSocketMessage     :   function(event) {
            console.log("[Web Socket] onSocketMessage -> " + event);
        },
        onSocketError       :   function(event) {
            console.log("[Web Socket] onSocketError -> " + event);
        }
        
    };
})();

Adaptilo.platform = (function() {
    "use strict";
    
    //  private attributs
    var mWebSocket;
    
    // constructor
    var AdaptiloPlatform = function(platformConfiguration) {   
        this.platformConfiguration = $.extend(true, {}, platformConfiguration);
    };
    
    // Public API
    AdaptiloPlatform.prototype = {
        connect :   function() {
            mWebSocket = new WebSocket('ws://'+this.platformConfiguration.server_ip+':'+this.platformConfiguration.server_port);
            mWebSocket.onopen    = this.platformConfiguration.onSocketOpen;
            mWebSocket.onclose   = this.platformConfiguration.onSocketClose;
            mWebSocket.onmessage = this.platformConfiguration.onSocketMessage;
            mWebSocket.onerror   = this.platformConfiguration.onSocketError;
        },
        
        setConfiguration : function(platformConfiguration) {
            this.platformConfiguration = $.extend(true, {}, platformConfiguration);
        }
    };

    return AdaptiloPlatform;    
})();
