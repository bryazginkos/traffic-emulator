package ru.traffic.messages.manage;

import ru.traffic.car.Car;
import ru.traffic.model.Position;

/**
 * Created by Константин on 01.07.2015.
 */
public class AddRoadPointClientMessage {
    private final Position position;
    private final Car car;

    public AddRoadPointClientMessage(Position position, Car car) {
        this.position = position;
        this.car = car;
    }

    public Position getPosition() {
        return position;
    }

    public Car getCar() {
        return car;
    }
}
