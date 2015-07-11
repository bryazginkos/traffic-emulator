package ru.traffic.view.json;

/**
 * Created by Константин on 01.07.2015.
 */
public class RoadPoint {
    private int distance;
    private int lane;
    private int wishSpeed;
    private double politeness;
    private double effrontery;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getLane() {
        return lane;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public int getWishSpeed() {
        return wishSpeed;
    }

    public void setWishSpeed(int wishSpeed) {
        this.wishSpeed = wishSpeed;
    }

    public double getPoliteness() {
        return politeness;
    }

    public void setPoliteness(double politeness) {
        this.politeness = politeness;
    }

    public double getEffrontery() {
        return effrontery;
    }

    public void setEffrontery(double effrontery) {
        this.effrontery = effrontery;
    }

    @Override
    public String toString() {
        return "RoadPoint{" +
                "distance=" + distance +
                ", lane=" + lane +
                ", wishSpeed=" + wishSpeed +
                ", politeness=" + politeness +
                ", effrontery=" + effrontery +
                '}';
    }
}
