package ru.traffic.car;

import com.google.common.base.Objects;

/**
 * Created by Константин on 01.07.2015.
 */
public class CarImpl implements Car {

    private double politeness;

    private double effrontery;

    private int wishSpeed;

    public CarImpl(double politeness, double effrontery, int wishSpeed) {
        this.politeness = politeness;
        this.wishSpeed = wishSpeed;
        this.effrontery = effrontery;
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
    public boolean willAsk() {
        return Math.random() < effrontery;
    }

    public double getPoliteness() {
        return politeness;
    }

    public double getEffrontery() {
        return effrontery;
    }

    @Override
    public String toString() {
        return "CarImpl{" +
                "politeness=" + politeness +
                ", wishSpeed=" + wishSpeed +
                ", effrontery=" + effrontery +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarImpl)) return false;
        CarImpl car = (CarImpl) o;
        return Objects.equal(politeness, car.politeness) &&
                Objects.equal(effrontery, car.effrontery) &&
                Objects.equal(wishSpeed, car.wishSpeed);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(politeness, effrontery, wishSpeed);
    }
}
