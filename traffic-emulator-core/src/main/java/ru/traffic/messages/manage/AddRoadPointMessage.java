package ru.traffic.messages.manage;

import ru.traffic.model.RoadPointInfo;

/**
 * Created by Константин on 01.07.2015.
 */
public class AddRoadPointMessage {

    //todo use position
    private final int distance;
    private final int lane;
    private final RoadPointInfo roadPointInfo;

    public AddRoadPointMessage(int distance, int lane, RoadPointInfo roadPointInfo) {
        this.distance = distance;
        this.lane = lane;
        this.roadPointInfo = roadPointInfo;
    }

    public int getDistance() {
        return distance;
    }

    public int getLane() {
        return lane;
    }

    public RoadPointInfo getRoadPointInfo() {
        return roadPointInfo;
    }
}
