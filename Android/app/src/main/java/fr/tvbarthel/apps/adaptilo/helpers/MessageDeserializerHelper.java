package fr.tvbarthel.apps.adaptilo.helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fr.tvbarthel.apps.adaptilo.models.Event;
import fr.tvbarthel.apps.adaptilo.models.enums.ControllerType;
import fr.tvbarthel.apps.adaptilo.models.enums.MessageType;
import fr.tvbarthel.apps.adaptilo.models.io.Message;
import fr.tvbarthel.apps.adaptilo.models.io.RegisterRoleResponse;

/**
 * Adapter for deserialization of {@link fr.tvbarthel.apps.adaptilo.models.io.Message}
 */
public class MessageDeserializerHelper implements JsonDeserializer<Message> {

    /**
     * json node key for external id
     */
    public static final String NODE_EXTERNAL_ID = "externalId";

    /**
     * json node key for message
     */
    public static final String NODE_MESSAGE = "message";

    /**
     * json node key for message type
     */
    public static final String NODE_TYPE = "type";

    /**
     * json node key for message content
     */
    public static final String NODE_CONTENT = "content";

    /**
     * json node key for game name
     */
    public static final String NODE_GAME_NAME = "gameName";

    /**
     * json node key for game room
     */
    public static final String NODE_GAME_ROOM = "gameRoom";

    /**
     * json node key for game role
     */
    public static final String NODE_GAME_ROLE = "gameRole";

    /**
     * json node key for replacement policy
     */
    public static final String NODE_SHOULD_REPLACE = "replace";

    /**
     * json node key for room creation policy
     */
    public static final String NODE_SHOULD_CREATE = "create";

    @Override
    public Message deserialize(JsonElement json, Type typeOfT,
                               JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        MessageType messageType = context.deserialize(jsonObject.get(NODE_TYPE), MessageType.class);

        Message message = new Message();
        message.setType(messageType);

        JsonElement content = jsonObject.get(NODE_CONTENT);

        switch (messageType) {
            case CONNECTION_COMPLETED:
                RegisterRoleResponse connectionResponse = context.deserialize(content, RegisterRoleResponse.class);
                message.setContent(connectionResponse);
                break;

            case VIBRATOR:
                Long duration = context.deserialize(content, Long.class);
                message.setContent(duration);
                break;

            case VIBRATOR_PATTERN:
                long[] pattern = context.deserialize(content, long[].class);
                message.setContent(pattern);
                break;

            case SENSOR:
                Event event = context.deserialize(content, Event.class);
                message.setContent(event);
                break;

            case ON_ROLE_UNREGISTERED:
                String unregisteredRole = context.deserialize(content, String.class);
                message.setContent(unregisteredRole);
                break;

            case ON_ROLE_REGISTERED:
                String registeredRole = context.deserialize(content, String.class);
                message.setContent(registeredRole);
                break;

            case ON_ROLES_RETRIEVED:
                TypeToken<ArrayList<String>> typeToken = new TypeToken<ArrayList<String>>() {
                };
                List<String> roles = context.deserialize(content,
                        typeToken.getType()
                );
                message.setContent(roles);
                break;

            case REPLACE_CONTROLLER:
                ControllerType type = context.deserialize(content, ControllerType.class);
                message.setContent(type);
                break;
            default:
                break;
        }
        return message;
    }
}
