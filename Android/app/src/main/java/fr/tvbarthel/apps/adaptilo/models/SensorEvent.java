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

    public SensorEvent() {
        super();
    }

    /**
     * create a sensor event timestamped with the current time
     *
     * @param type
     * @param action
     * @param value
     */
    public SensorEvent(EventType type, EventAction action, double value) {
        super(type, action);
        mEventValue = value;
    }

    public double getEventValue() {
        return mEventValue;
    }

    public void setEventValue(double mEventValue) {
        this.mEventValue = mEventValue;
    }
}
