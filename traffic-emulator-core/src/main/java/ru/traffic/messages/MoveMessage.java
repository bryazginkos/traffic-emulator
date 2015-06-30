package ru.traffic.messages;

import ru.traffic.model.Position;

/**
 * Created by Константин on 30.06.2015.
 */
public class MoveMessage {

    private final Position positionFrom;
    private final Position positionTo;

    public MoveMessage(Position positionFrom, Position positionTo) {
        this.positionFrom = positionFrom;
        this.positionTo = positionTo;
    }

}
