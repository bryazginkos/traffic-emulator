package ru.traffic.messages;

import ru.traffic.model.RoadPointInfo;
import ru.traffic.util.RoadArray;

/**
 * Created by Константин on 30.06.2015.
 */
public class NextTimeMessage {

    private final RoadArray<RoadPointInfo> state;

    public NextTimeMessage(RoadArray<RoadPointInfo> state) {
        this.state = state;
    }

    public RoadArray<RoadPointInfo> getState() {
        return state;
    }
}
