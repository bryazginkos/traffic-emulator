package ru.traffic.view.json;

/**
 * Created by Константин on 01.07.2015.
 */
public class Road {

    private String[][] points;

    private int lanes;

    private int length;

    public String[][] getPoints() {
        return points;
    }

    public void setPoints(String[][] points) {
        this.points = points;
    }

    public int getLanes() {
        return lanes;
    }

    public void setLanes(int lanes) {
        this.lanes = lanes;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
