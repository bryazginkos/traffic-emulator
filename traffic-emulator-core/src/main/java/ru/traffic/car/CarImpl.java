package ru.traffic.car;

/**
 * Created by Константин on 01.07.2015.
 */
public class CarImpl implements Car {

    private double politeness;

    private int wishSpeed;

    private SlowFrontAction slowFrontAction;

    private StopFrontAction stopFrontAction;

    public CarImpl(double politeness, int wishSpeed, SlowFrontAction slowFrontAction, StopFrontAction stopFrontAction) {
        this.politeness = politeness;
        this.wishSpeed = wishSpeed;
        this.slowFrontAction = slowFrontAction;
        this.stopFrontAction = stopFrontAction;
    }

    @Override
    public boolean askSkip() {
        return Math.random() < politeness;
    }


    @Override
    public SlowFrontAction slowFront() {
        return slowFrontAction;
    }

    @Override
    public StopFrontAction stopFront() {
        return stopFrontAction;
    }

    @Override
    public int wishSpeed() {
        return wishSpeed;
    }

    @Override
    public String toString() {
        return "CarImpl{" +
                "politeness=" + politeness +
                ", wishSpeed=" + wishSpeed +
                ", slowFrontAction=" + slowFrontAction +
                ", stopFrontAction=" + stopFrontAction +
                '}';
    }
}
