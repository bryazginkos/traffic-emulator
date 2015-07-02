package ru.traffic.view.json;

/**
 * Created by Константин on 01.07.2015.
 */
public class RoadPoint {
    private int distance;
    private int lane;
    private int wishSpeed;
    private String slowFrontAction;
    private String stopFrontAction;
    private double politeness;

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

    public String getSlowFrontAction() {
        return slowFrontAction;
    }

    public void setSlowFrontAction(String slowFrontAction) {
        this.slowFrontAction = slowFrontAction;
    }

    public String getStopFrontAction() {
        return stopFrontAction;
    }

    public void setStopFrontAction(String stopFrontAction) {
        this.stopFrontAction = stopFrontAction;
    }

    public double getPoliteness() {
        return politeness;
    }

    public void setPoliteness(double politeness) {
        this.politeness = politeness;
    }

    @Override
    public String toString() {
        return "RoadPoint{" +
                "distance=" + distance +
                ", lane=" + lane +
                ", wishSpeed=" + wishSpeed +
                ", slowFrontAction='" + slowFrontAction + '\'' +
                ", stopFrontAction='" + stopFrontAction + '\'' +
                ", politeness=" + politeness +
                '}';
    }
}
