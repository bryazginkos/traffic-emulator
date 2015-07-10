package ru.traffic.car;

/**
 * Created by Константин on 01.07.2015.
 */
public class CarImpl implements Car {

    private double politeness;

    private int wishSpeed;

    public CarImpl(double politeness, int wishSpeed) {
        this.politeness = politeness;
        this.wishSpeed = wishSpeed;
    }

    @Override
    public boolean askSkip() {
        return Math.random() < politeness;
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
                '}';
    }
}
