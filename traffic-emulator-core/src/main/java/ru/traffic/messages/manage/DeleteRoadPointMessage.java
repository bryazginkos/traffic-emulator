package ru.traffic.messages.manage;

import ru.traffic.model.Position;

/**
 * Created by Константин on 01.07.2015.
 */
public class DeleteRoadPointMessage {
    private final Position position;

    public DeleteRoadPointMessage(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
