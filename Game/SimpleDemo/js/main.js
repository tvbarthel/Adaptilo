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
 *  Create a default configuration
 *  var myConfiguration = Adaptilo.Configuration.createDefault();
 *
 *  Customize your configuration. You can modify :
 *
 *          myConfiguration.serverIp -> the ip of the server.
 *          myConfiguration.serverPort -> the port of the server.
 *          myConfiguration.gameName -> the name of the game.
 *          myConfiguration.gameRole -> the role to use.
 *          myConfiguration.gameRoom -> the name of the room to join.
 *          myConfiguration.createRoom -> true if the room should be created if it does not exist, false otherwise.
 *          myConfiguration.replaceRoom -> true if the room should be replaces if it already exists, false otherwise.
 *          myConfiguration.onConnected -> a function that is called when the platform is connected. This function takes one parameter : an array of roles. Each role is a simple string.
 *          myConfiguration.onMessege -> a function that is called when the platform receives a message. This function takes one parameter : the message received.
 *          myConfiguration.onError -> a function that is called when the platform experiences an error. This function takes one parameter : the error that occurred. 
 *
 *  Instantiate a new platform.
 *  var myPlatform = Adaptilo.platform(myConfiguration);
 *
 *  Connect the platform to the server.
 *  myPlatform.connect();
 *
 */

var Adaptilo = Adaptilo || {};

Adaptilo.Configuration = (function() {
    "use strict";
    return { createDefault : function() {
        return {
            serverIp            :   "192.168.0.1",
            serverPort          :   "8080",
            gameName            :   "defaultGameName",
            gameRole            :   "field",
            gameRoom            :   "defaultRoom",
            createRoom          :   true,
            replaceRoom         :   true,
            onConnected         :   function(roles) {
                console.log("onConnected");
                console.log(roles);
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
                // The web socket is now opened
                // Send a registration request as field
                var registerRoleMessage = new Adaptilo.Message(Adaptilo.Message.Type.REGISTER_ROLE_REQUEST, 
                    {
                        gameName : that.platformConfiguration.gameName,
                        gameRole : that.platformConfiguration.gameRole,
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
					//store external id assigned by the server
                    that.externalId = message.content.externalId;
					console.log("Game room : "+message.content.gameRoom);
                    // Send a message to get the available roles
                    var rolesRequest = new Adaptilo.Message(Adaptilo.Message.Type.ROLE_REQUEST, {});
                    that.sendMessage(rolesRequest);
                } else if (message.type === Adaptilo.Message.Type.ON_ROLE_RETRIEVED) {
                    that.platformConfiguration.onConnected(message.content);
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
        ROLE_REQUEST            :   "ROLES_REQUEST",
        ON_ROLE_RETRIEVED       :   "ON_ROLES_RETRIEVED",
    }
})();
