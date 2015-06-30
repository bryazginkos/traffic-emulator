package ru.traffic.messages;

import ru.traffic.model.Position;

/**
 * Created by Константин on 30.06.2015.
 */
public class RegistrationCarMessage {

    private final Position position;

    private final int speed;

    public RegistrationCarMessage(Position position, int speed) {
        this.position = position;
        this.speed = speed;
    }

    public Position getPosition() {
        return position;
    }

    public int getSpeed() {
        return speed;
    }
}
