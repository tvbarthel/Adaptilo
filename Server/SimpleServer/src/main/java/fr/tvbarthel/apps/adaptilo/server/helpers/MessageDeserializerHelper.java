package fr.tvbarthel.apps.adaptilo.server.helpers;

import com.google.gson.*;
import fr.tvbarthel.apps.adaptilo.server.models.Event;
import fr.tvbarthel.apps.adaptilo.server.models.Message;
import fr.tvbarthel.apps.adaptilo.server.models.MessageType;
import fr.tvbarthel.apps.adaptilo.server.models.RegisterControllerRequest;

import java.lang.reflect.Type;


/**
 * Adapter for deserialization of {@link Message}
 */
public class MessageDeserializerHelper implements JsonDeserializer<Message> {

    /**
     * json node key for message type
     */
    public static final String NODE_TYPE = "type";

    /**
     * json node key for message content
     */
    public static final String NODE_CONTENT = "content";

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        MessageType messageType = context.deserialize(jsonObject.get(NODE_TYPE), MessageType.class);

        Message message = new Message();
        message.setType(messageType);

        JsonElement content = jsonObject.get(NODE_CONTENT);

        switch (messageType) {
            case CONNECTION_COMPLETED:
                String connectionId = context.deserialize(content, String.class);
                message.setContent(connectionId);
                break;

            case VIBRATOR:
                Long duration = context.deserialize(content, Long.class);
                message.setContent(duration);
                break;

            case VIBRATOR_PATTERN:
                long[] pattern = context.deserialize(content, long[].class);
                message.setContent(pattern);
                break;

            case REGISTER_CONTROLLER:
                RegisterControllerRequest registrationRequest = context.deserialize(content, RegisterControllerRequest.class);
                message.setContent(registrationRequest);
                break;

            case SENSOR:
                Event event = context.deserialize(content, Event.class);
                message.setContent(event);
                break;
        }
        return message;
    }
}
