package fr.tvbarthel.apps.adaptilo.models;

import fr.tvbarthel.apps.adaptilo.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;

/**
 * Encapsulate Event data
 */
public class Event {

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
     * Default constructor.
     */
    public Event() {

    }

    /**
     * Constructor.
     * <p/>
     * {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType}
     * {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction}
     *
     * @param type   Event type
     * @param action Event action
     */
    public Event(EventType type, EventAction action) {
        mEventType = type;
        mEventAction = action;
    }

    /**
     * Retrieve the event type.
     *
     * @return event type of the given event.
     */
    public EventType getEventType() {
        return mEventType;
    }

    /**
     * Define the event type of the current event.
     *
     * @param mEventType new event type.
     */
    public void setEventType(EventType mEventType) {
        this.mEventType = mEventType;
    }

    /**
     * Retrieve the event action.
     *
     * @return event action of the given event.
     */
    public EventAction getEventAction() {
        return mEventAction;
    }

    /**
     * Define the event action of the current event.
     *
     * @param mEventAction new event action.
     */
    public void setEventAction(EventAction mEventAction) {
        this.mEventAction = mEventAction;
    }
}
