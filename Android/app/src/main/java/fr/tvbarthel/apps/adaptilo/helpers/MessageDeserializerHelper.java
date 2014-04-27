package fr.tvbarthel.apps.adaptilo.helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import fr.tvbarthel.apps.adaptilo.models.Message;
import fr.tvbarthel.apps.adaptilo.models.MessageType;

/**
 * Adapter for deserialization of {@link fr.tvbarthel.apps.adaptilo.models.Message}
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

        switch (messageType) {
            case CONNECTION_COMPLETED:
                JsonElement content = jsonObject.get(NODE_CONTENT);
                Integer connectionId = context.deserialize(content, Integer.class);
                message.setContent(connectionId);
                break;
        }
        return message;
    }
}
