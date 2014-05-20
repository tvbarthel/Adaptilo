package fr.tvbarthel.apps.adaptilo.server.models;

/**
 * Encapsulate event data
 */
public class Event {

    /**
     * type of the event, KEY_A for instance.
     * {@link EventType}
     */
    protected EventType mEventType;

    /**
     * action performed for the given type, ACTION_KEY_DOWN for instance.
     * {@link EventAction}
     */
    protected EventAction mEventAction;

    public Event() {

    }

    public Event(EventType type, EventAction action) {
        mEventType = type;
        mEventAction = action;
    }

    public EventType getEventType() {
        return mEventType;
    }

    public void setEventType(EventType mEventType) {
        this.mEventType = mEventType;
    }

    public EventAction getEventAction() {
        return mEventAction;
    }

    public void setEventAction(EventAction mEventAction) {
        this.mEventAction = mEventAction;
    }
}
