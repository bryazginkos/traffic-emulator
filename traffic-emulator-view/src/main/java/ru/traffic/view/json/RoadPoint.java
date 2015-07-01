package ru.traffic.view.json;

/**
 * Created by ���������� on 01.07.2015.
 */
public class RoadPoint {
    private int distance;
    private int lane;
    private int wishspeed;
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

    public int getWishspeed() {
        return wishspeed;
    }

    public void setWishspeed(int wishspeed) {
        this.wishspeed = wishspeed;
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
}