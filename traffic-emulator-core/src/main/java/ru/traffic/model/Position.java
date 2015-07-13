package ru.traffic.model;

import com.google.common.base.Objects;

/**
 * Created by Константин on 30.06.2015.
 */
public class Position {
    private final int distance;
    private final int lane;

    public Position(int distance, int lane) {
        this.distance = distance;
        this.lane = lane;
    }

    public int getDistance() {
        return distance;
    }

    public int getLane() {
        return lane;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return Objects.equal(distance, position.distance) &&
                Objects.equal(lane, position.lane);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(distance, lane);
    }

    @Override
    public String toString() {
        return "Position{" +
                "distance=" + distance +
                ", lane=" + lane +
                '}';
    }
}
