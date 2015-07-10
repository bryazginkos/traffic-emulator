package ru.traffic.util;

import ru.traffic.model.Position;

/**
 * Created by Константин on 10.07.2015.
 */
public class PositionUtil {
    public static Position getLeft(Position position) {
        return new Position(position.getDistance(), position.getLane() - 1);
    }

    public static Position getRight(Position position) {
        return new Position(position.getDistance(), position.getLane() + 1);
    }

    public static Position move(Position start, int deltaDist, int deltaLane) {
        return new Position(start.getDistance() + deltaDist, start.getLane() + deltaLane);
    }
}
