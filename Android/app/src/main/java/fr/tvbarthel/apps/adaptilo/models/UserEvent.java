package fr.tvbarthel.apps.adaptilo.models;

import fr.tvbarthel.apps.adaptilo.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;

/**
 * Encapsulate user event.
 * <p/>
 * A simple Event with a timestamp in addition.
 */
public class UserEvent extends Event {

    /**
     * timestamp of the event
     */
    private Long mEventTimestamp;

    /**
     * default constructor
     */
    public UserEvent() {
        super();
    }

    /**
     * create a user event timestamped with the current time
     *
     * @param type   event type.
     * @param action event action.
     */
    public UserEvent(EventType type, EventAction action) {
        super(type, action);
        mEventTimestamp = System.currentTimeMillis();
    }

    /**
     * Retrieve the timestamp associated to the event.
     *
     * @return time stamp in milli.
     */
    public Long getEventTimestamp() {
        return mEventTimestamp;
    }

    /**
     * Set a new time stamp for the user event.
     *
     * @param mEventTimestamp new time stamp in milli.
     */
    public void setEventTimestamp(Long mEventTimestamp) {
        this.mEventTimestamp = mEventTimestamp;
    }
}
