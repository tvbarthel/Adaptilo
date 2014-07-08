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
 *  var myConfiguration = Adaptilo.defaultConfiguration.create();
 *
 *  Instantiate a new platform.
 *  var myPlatform = Adaptilo.platform(myConfiguration);
 *
 *  Connect the platform to the server.
 *  myPlatform.connect();
 *
 */

var Adaptilo = Adaptilo || {};

Adaptilo.DefaultConfiguration = (function() {
    "use strict";
    return { create : function() {
        return {
            serverIp            :   "192.168.0.1",
            serverPort          :   "8080",
            gameName            :   "defaultGameName",
            gameRole            :   "field",
            gameRoom            :   "defaultRoom",
            createRoom          :   true,
            replaceRoom         :   true,
            onConnected         :   function() {
                console.log("onConnected");
            },
            onMessage           :   function(event) {
                console.log("onMessage");
                console.log(event);                
            },
            onError             :   function(event) {
                console.log("onError");
                console.log(event);                
            }        
        }
    }};
})();

Adaptilo.Platform = (function() {
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
            var that = this;
            mWebSocket = new WebSocket('ws://'+this.platformConfiguration.serverIp+':'+this.platformConfiguration.serverPort);
            
            // Setup the onopen behavior
            mWebSocket.onopen    = function() {
                // The web socket is now openned
                // Send a registration request as field
                var registerRoleMessage = new Adaptilo.Message(Adaptilo.Message.Type.REGISTER_ROLE_REQUEST, 
                    {
                        gameName : that.platformConfiguration.gameName,
                        gameRole : that.platformConfiguration.gameRole,
                        gameRoom : that.platformConfiguration.gameRoom,
                        create   : that.platformConfiguration.createRoom,
                        replace  : that.platformConfiguration.replaceRoom,
                });                
                that.sendMessage(registerRoleMessage);
            };
            
            // Setup the onclose behavior
            mWebSocket.onclose   = function(event) {
                console.log("[Web Socket] onclose");
                console.log(event); 
            };
            
            // Setup the onmessage behavior
            mWebSocket.onmessage = function(event) {
                var message = JSON.parse(event.data);
                if (message.type === Adaptilo.Message.Type.CONNECTION_COMPLETED) {
                    that.externalId = message.content;
                    that.platformConfiguration.onConnected();
                } else {
                    that.platformConfiguration.onMessage(message);
                }                
            };
            
            // Setup the onerror behavior
            mWebSocket.onerror   = this.platformConfiguration.onError;
        },
        
        setConfiguration : function(platformConfiguration) {
            this.platformConfiguration = $.extend(true, {}, platformConfiguration);
        },
        
        sendMessage : function(message) {        
            var serverRequest = new Adaptilo.ServerRequest(this.externalId, message);            
            mWebSocket.send(JSON.stringify(serverRequest));
        }
    };

    return AdaptiloPlatform;    
})();

Adaptilo.ServerRequest = (function() {
    "use strict";
    
    // Constructor
    var ServerRequest = function(externalId, message) {
        this.externalId = externalId;
        this.message = message;
    };
    
    return ServerRequest;
})();

Adaptilo.Message = (function() {
    "use strict";   
    
    // Constructor
    var Message = function(type, content) {
        this.type = type;
        this.content = content;
    };   
    
    return Message;    
})();

Adaptilo.Message.Type = (function() {
    return {
        REGISTER_ROLE_REQUEST   :   "REGISTER_ROLE_REQUEST",
        CONNECTION_COMPLETED    :   "CONNECTION_COMPLETED",
    }
})();
