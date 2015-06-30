package ru.traffic.messages;

import ru.traffic.util.RoadArray;

/**
 * Created by Константин on 30.06.2015.
 */
public class NextTimeMessage {

    private final RoadArray state;

    public NextTimeMessage(RoadArray state) {
        this.state = state;
    }

    public RoadArray getState() {
        return state;
    }
}
