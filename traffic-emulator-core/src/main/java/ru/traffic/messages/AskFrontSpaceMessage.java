package ru.traffic.messages;

import ru.traffic.model.Position;

/**
 * Created by Константин on 30.06.2015.
 */
public class AskFrontSpaceMessage {

    private final Position position;
    private final int interval;

    public AskFrontSpaceMessage(Position position, int interval) {
        this.position = position;
        this.interval = interval;
    }

    public Position getPosition() {
        return position;
    }

    public int getInterval() {
        return interval;
    }
}
