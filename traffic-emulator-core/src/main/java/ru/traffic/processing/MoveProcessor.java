package ru.traffic.processing;

import ru.traffic.exception.AccidentException;
import ru.traffic.model.RoadPointInfo;
import ru.traffic.model.Move;
import ru.traffic.model.Position;
import ru.traffic.util.RoadArray;

import java.util.Collection;

/**
 * Created by Константин on 30.06.2015.
 */
public class MoveProcessor {

    public static void processing(RoadArray state, Collection<Move> moveCollection) throws AccidentException {
        //todo check params
        for (Move move : moveCollection) {
            doMove(state, move);
        }
    }

    private static void doMove(RoadArray<RoadPointInfo> state, Move move) throws AccidentException {
        Position from = move.getFrom();
        Position to = move.getTo();
        if (from.equals(to)) {
            return;
        }
        //todo assert that not null
        RoadPointInfo roadPointInfo = state.get(from.getDistance(), from.getLane());
        if (state.get(to.getDistance(), to.getLane()) != null) {
            //todo create advanced check for accidents (trajectory's intersections)
            throw new AccidentException("Accident at " + new Position(to.getDistance(), to.getLane()).toString());
        }
        state.put(to.getDistance(), to.getLane(), roadPointInfo);
        state.put(from.getDistance(), from.getLane(), null);

        roadPointInfo.setSpeed(to.getDistance() - from.getDistance());
    }
}
