package ru.traffic.messages.manage;

import ru.traffic.model.Position;

/**
 * Created by Константин on 01.07.2015.
 */
public class AddRoadPointClientMessage {
    private final Position position;
    private final int wishSpeed;

    public AddRoadPointClientMessage(Position position, int wishSpeed) {
        this.position = position;
        this.wishSpeed = wishSpeed;
    }

    public Position getPosition() {
        return position;
    }

    public int getWishSpeed() {
        return wishSpeed;
    }
}
