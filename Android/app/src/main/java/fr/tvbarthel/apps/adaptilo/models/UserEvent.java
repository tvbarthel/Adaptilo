package fr.tvbarthel.apps.adaptilo.models;

import fr.tvbarthel.apps.adaptilo.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;

/**
 * Encapsulate user event
 */
public class UserEvent extends Event {

    /**
     * timestamp of the event
     */
    protected Long mEventTimestamp;

    /**
     * default constructor
     */
    public UserEvent() {
        super();
    }

    /**
     * create a user event timestamped with the current time
     *
     * @param type
     * @param action
     */
    public UserEvent(EventType type, EventAction action) {
        super(type, action);
        mEventTimestamp = System.currentTimeMillis();
    }

    public Long getEventTimestamp() {
        return mEventTimestamp;
    }

    public void setEventTimestamp(Long mEventTimestamp) {
        this.mEventTimestamp = mEventTimestamp;
    }
}
