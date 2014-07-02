Adaptilo Server
========

Adaptilo, from Esperanto 'controller', 'adapter'.


API
========

## **Network message**

### Scheme

* Client to Server :
<pre>
    Actor1 |--------ServerRequest---------- Server
    Actor1 -----------Message--------------|Server
</pre>

* Server to Client :
<pre>
    Actor1 |-----------Message------------- Server
</pre>

* Client to Server to Clients :
<pre>
    Actor1 |--------ServerRequest---------- Server |-----------Message------------- Actor2
                                                   |-----------Message------------- Actor3
</pre>

### Model

* Message 
```java
        int type : type of message {@see MessageType section}
        Object content : content of message
```
*Example* 
```json
    {
        "type" : "USER_INPUT",
        "content" : {
                      "eventAction" : "KEY_A",
                      "eventTimeStamp" : 1403207477362,
    }
```

* ServerRequest
```java
        String externalId : id used to identify each client, null if first connection
        Message message: content of the request
```
*Example*
```json
    {
        "externalId" : "/testGamevirtualcontroller1921394365",
        "message" : {
                       "type" : "USER_INPUT",
                       "content" : {
                                     "eventAction" : "KEY_A",
                                     "eventTimeStamp" : 1403207477362,
                        }
        }
    }
```

## **Message Types**
```java
    /**
     * send by the server to replace the controller
     */
    REPLACE_CONTROLLER,

    /**
     * send by the server to communicate client id
     */
    CONNECTION_COMPLETED,

    /**
     * send by the server to perform vibration
     */
    VIBRATOR,

    /**
     * send by the server to perform vibration pattern
     */
    VIBRATOR_PATTERN,

    /**
     * send by the client when user perform an input, press a key for instance
     */
    USER_INPUT,

    /**
     * send by the client when it's ready.
     */
    ENGINE_READY,

    /**
     * send by the client when game should be paused
     */
    PAUSE,

    /**
     * send by the client when game should be resumed
     */
    RESUME,

    /**
     * send by the client when a game has been loaded
     */
    REGISTER_CONTROLLER,

    /**
     * send by the client when sensor events are fired
     */
    SENSOR

```

## **Closing error**

Error code used when connection is closed. Websocket.close(int code)

```java

    ////////////////////////////////////////
    /////////// REGISTRATION ERROR /////////
    ////////////////////////////////////////

    /**
     * Requested game name doesn't exist.
     */
    public static final int REGISTRATION_REQUESTED_GAME_NAME_UNKNOWN = 2000;

    /**
     * No rooms have been created yet for the requested game name.
     */
    public static final int REGISTRATION_NO_ROOM_CREATED = 2001;

    /**
     * Requested room id doesn't exist.
     */
    public static final int REGISTRATION_REQUESTED_ROOM_UNKNOW = 2003;

    /**
     * Requested role doesn't exist for the given game.
     */
    public static final int REGISTRATION_REQUESTED_ROLE_UNKNOWN = 2004;

    /**
     * Requested room is empty.
     */
    public static final int REGISTRATION_REQUESTED_ROOM_IS_EMPTY = 2005;

```
