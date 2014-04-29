package fr.tvbarthel.apps.adaptilo.models;

import fr.tvbarthel.apps.adaptilo.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;

/**
 * Encapsulate user event
 */
public class UserEvent {

    /**
     * type of the event, KEY_A for instance.
     * {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType}
     */
    private EventType mEventType;

    /**
     * action performed for the given type, ACTION_KEY_DOWN for instance.
     * {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction}
     */
    private EventAction mEventAction;

    /**
     * timestamp of the event
     */
    private Long mEventTimestamp;

    /**
     * default constructor
     */
    public UserEvent() {

    }

    /**
     * create a user event timestamped with the current time
     *
     * @param type
     * @param action
     */
    public UserEvent(EventType type, EventAction action) {
        mEventType = type;
        mEventAction = action;
        mEventTimestamp = System.currentTimeMillis();
    }

    public EventType getmEventType() {
        return mEventType;
    }

    public void setmEventType(EventType mEventType) {
        this.mEventType = mEventType;
    }

    public EventAction getmEventAction() {
        return mEventAction;
    }

    public void setmEventAction(EventAction mEventAction) {
        this.mEventAction = mEventAction;
    }

    public Long getmEventTimestamp() {
        return mEventTimestamp;
    }

    public void setmEventTimestamp(Long mEventTimestamp) {
        this.mEventTimestamp = mEventTimestamp;
    }
}
