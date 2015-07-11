package ru.traffic.model;

import akka.actor.ActorRef;
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

    //todo normal methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoadPointInfo that = (RoadPointInfo) o;

        if (speed != that.speed) return false;
        return !(actorRef != null ? !actorRef.equals(that.actorRef) : that.actorRef != null);

    }

    @Override
    public int hashCode() {
        int result = speed;
        result = 31 * result + (actorRef != null ? actorRef.hashCode() : 0);
        return result;
    }
}
