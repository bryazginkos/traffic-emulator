package ru.traffic.model;

import akka.actor.ActorRef;
import com.google.common.base.Objects;
import ru.traffic.car.Car;

/**
 * Created by Константин on 30.06.2015.
 */
public class RoadPointInfo {
    private int speed;
    private final ActorRef actorRef;
    private final Car car;

    public RoadPointInfo(Car car, ActorRef actorRef) {
        this.car = car;
        this.speed = car.wishSpeed();
        this.actorRef = actorRef;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }

    public Car getCar() {
        return car;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoadPointInfo)) return false;
        RoadPointInfo that = (RoadPointInfo) o;
        return Objects.equal(speed, that.speed) &&
                Objects.equal(actorRef, that.actorRef) &&
                Objects.equal(car, that.car);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(speed, actorRef, car);
    }
}
