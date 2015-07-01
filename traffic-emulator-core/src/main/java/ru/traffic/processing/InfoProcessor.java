package ru.traffic.processing;

import ru.traffic.exception.OutOfViewException;
import ru.traffic.model.Position;
import ru.traffic.util.RoadArray;

/**
 * Created by Константин on 30.06.2015.
 */
public class InfoProcessor {

    public static int freeFrontSpace(RoadArray state, Position position, int Maxinterval) throws OutOfViewException {
        //todo check params
        int viewInterval = 0;
        int distance = position.getDistance();
        int lane = position.getLane();

        while (viewInterval < Maxinterval) {
            viewInterval++;
            int viewDistance = distance + viewInterval;
            if (viewDistance > state.getLength()) {
                throw new OutOfViewException();
            }
            if (state.get(viewDistance, lane) != null) {
                return viewInterval - 1;
            }
        }
        return Maxinterval;
    }
}
