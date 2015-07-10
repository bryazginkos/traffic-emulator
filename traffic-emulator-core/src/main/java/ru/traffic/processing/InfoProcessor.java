package ru.traffic.processing;

import ru.traffic.model.Position;
import ru.traffic.util.RoadArray;

/**
 * Created by Константин on 30.06.2015.
 */
//todo must be actor
public class InfoProcessor {

    public static Integer freeFrontSpace(RoadArray state, Position position, int Maxinterval) {
        //todo check params
        int viewInterval = 0;
        int distance = position.getDistance();
        int lane = position.getLane();

        while (viewInterval < Maxinterval) {
            viewInterval++;
            int viewDistance = distance + viewInterval;
            if (viewDistance > state.getLength()) {
                return null;
            }
            if (state.get(viewDistance, lane) != null) {
                return viewInterval - 1;
            }
        }
        return Maxinterval;
    }
}
