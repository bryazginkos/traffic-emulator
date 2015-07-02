package ru.traffic.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ru.traffic.messages.NextTimeMessage;
import ru.traffic.messages.manage.AddRoadPointMessage;
import ru.traffic.messages.manage.DeleteRoadPointMessage;
import ru.traffic.messages.move.MoveMessage;
import ru.traffic.messages.move.MovesMessage;
import ru.traffic.model.Move;
import ru.traffic.model.Position;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Константин on 30.06.2015.
 */
public class DecisionActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ActorRef roadActor;

    private int waitingMoves;

    private Map<ActorRef, Move> movesMap;

    public DecisionActor(ActorRef roadActor) {
        this.roadActor = roadActor;
        movesMap = new HashMap<>();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof NextTimeMessage) {
            nextTime((NextTimeMessage)o);
        } else if (o instanceof MoveMessage) {
            takeMove((MoveMessage)o);
        } else if (o instanceof AddRoadPointMessage) {
            addRoadPoint((AddRoadPointMessage)o);
        } else if (o instanceof DeleteRoadPointMessage) {
            deleteRoadPoint((DeleteRoadPointMessage)o);
        } else {
            unhandled(o);
        }
    }

    private void nextTime(NextTimeMessage nextTimeMessage) {
        log.info("nextTime");
        waitingMoves = nextTimeMessage.getState().getElementsNum();
        movesMap.entrySet().forEach(entry -> entry.getKey().tell(nextTimeMessage, getSelf()));
        movesMap = new HashMap<>(waitingMoves);
    }

    private void takeMove(MoveMessage moveMessage) {
        log.info("take move decision move=" + moveMessage.getMove());
        waitingMoves--;
        movesMap.put(getSender(), moveMessage.getMove());
        log.info("waiting for " + waitingMoves + "moves to process moves");
        if (waitingMoves == 0) {
            sendMoves();
        }
    }

    private void sendMoves() {
        log.info("send decisions");
        MovesMessage movesMessage = new MovesMessage(movesMap.values());
        roadActor.tell(movesMessage, getSelf());
    }

    private void addRoadPoint(AddRoadPointMessage addRoadPointMessage) {
        int distance = addRoadPointMessage.getDistance();
        int lane = addRoadPointMessage.getLane();
        Position position = new Position(distance, lane);

        log.info("decision add roadPoint: position=" + position + " wishSpeed=" + addRoadPointMessage.getRoadPointInfo().getSpeed());
        roadActor.tell(addRoadPointMessage, getSender());

        Move move = new Move(position, position);
        //to catch accident
        movesMap.put(getSender(), move);
        log.info("waiting for " + waitingMoves + "moves to process moves");
        if (waitingMoves == 0) {
            sendMoves();
        }
    }

    private void deleteRoadPoint(DeleteRoadPointMessage deleteRoadPointMessage) {
        log.info("decision delete roadPoint: position=" + deleteRoadPointMessage.getPosition());
        roadActor.tell(deleteRoadPointMessage, getSender());
        movesMap.remove(getSender());
        waitingMoves--;
        log.info("waiting for " + waitingMoves + "moves to process moves");
        if (waitingMoves == 0) {
            sendMoves();
        }
    }
}
