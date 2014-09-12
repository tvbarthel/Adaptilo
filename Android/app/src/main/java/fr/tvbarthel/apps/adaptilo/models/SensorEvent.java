package fr.tvbarthel.apps.adaptilo.models;

import fr.tvbarthel.apps.adaptilo.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;

/**
 * encapsulate sensor event
 */
public class SensorEvent extends UserEvent {

    /**
     * value of the detected event
     */
    private double mEventValue;

    /**
     * Empty constructor.
     */
    public SensorEvent() {
        super();
    }

    /**
     * Create a sensor event timestamped with the current time.
     *
     * @param type   event type.
     * @param action event action.
     * @param value  event value.
     */
    public SensorEvent(EventType type, EventAction action, double value) {
        super(type, action);
        mEventValue = value;
    }

    /**
     * Retrieve the current value of the event.
     *
     * @return value associated with the event.
     */
    public double getEventValue() {
        return mEventValue;
    }

    /**
     * Set a new value for the given event.
     *
     * @param mEventValue new event value.
     */
    public void setEventValue(double mEventValue) {
        this.mEventValue = mEventValue;
    }
}
