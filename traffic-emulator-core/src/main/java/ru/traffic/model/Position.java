package ru.traffic.model;

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

    //todo normal equals and hashcode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (distance != position.distance) return false;
        return lane == position.lane;

    }

    @Override
    public int hashCode() {
        int result = distance;
        result = 31 * result + lane;
        return result;
    }

    @Override
    public String toString() {
        return "Position{" +
                "distance=" + distance +
                ", lane=" + lane +
                '}';
    }
}
