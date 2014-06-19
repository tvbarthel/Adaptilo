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
    Actor1 --------ServerResponse----------|Server
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
        String content : content of message
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

* ServerResponse
```java
        int status : status of the response {@see ServerResponseStatus section}
        Message message: content of the request
```
*Example* 
```json
    {
        "status" : 0,
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

## **Message Types**

* SUCCEED : 0
* FAILURE : 1
* INTERNAL_ERROR : 2


