package ru.traffic.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ru.traffic.messages.NextTimeMessage;
import ru.traffic.messages.manage.AddRoadPointMessage;
import ru.traffic.messages.manage.DeleteRoadPointMessage;
import ru.traffic.messages.manage.ErrorAddRoadPointMessage;
import ru.traffic.messages.move.MoveMessage;
import ru.traffic.messages.move.MovesMessage;
import ru.traffic.model.Move;
import ru.traffic.model.Position;
import ru.traffic.model.RoadPointInfo;

import java.util.*;

/**
 * Created by Константин on 30.06.2015.
 */
public class DecisionActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ActorRef roadActor;

    private ActorRef managerActor;

    private int waitingMoves;

    private Map<ActorRef, Move> movesMap;

    private List<AddRoadPointMessage> buffer;

    private Phase phase;

    public DecisionActor(ActorRef roadActor, ActorRef managerActor) {
        this.roadActor = roadActor;
        this.managerActor = managerActor;
        movesMap = new HashMap<>();
        buffer = new ArrayList<>();
        phase = Phase.WAITING_INITILIZATION;
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
        } else if (o instanceof ErrorAddRoadPointMessage) {
            errorAddRoadPoint((ErrorAddRoadPointMessage)o);
        } else {
            unhandled(o);
        }
    }

    private void nextTime(NextTimeMessage nextTimeMessage) {
        phase = Phase.COLLECTING_MOVES;
        log.info("nextTime (waiting for " + nextTimeMessage.getState().getElementsNum() +" moves and  + " + buffer.size() + " moves will add from new points)");
        waitingMoves = nextTimeMessage.getState().getElementsNum();
        buffer.forEach(addRoadPointMessage -> addRoadPoint(addRoadPointMessage));
        buffer.clear();
        movesMap.entrySet().forEach(entry -> entry.getKey().tell(nextTimeMessage, getSelf()));
        movesMap = new HashMap<>(waitingMoves);
        phase = Phase.COLLECTING_MOVES;
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
        phase = Phase.WAITING_PROCESSING;
        roadActor.tell(movesMessage, getSelf());
    }

    private void addRoadPoint(AddRoadPointMessage addRoadPointMessage) {
        int distance = addRoadPointMessage.getDistance();
        int lane = addRoadPointMessage.getLane();
        Position position = new Position(distance, lane);

        RoadPointInfo roadPointInfo = addRoadPointMessage.getRoadPointInfo();
        log.info("decision add roadPoint: position=" + position + " wishSpeed=" + roadPointInfo.getSpeed());

        switch (phase) {
            case COLLECTING_MOVES:
            case WAITING_INITILIZATION:{
                roadActor.tell(addRoadPointMessage, getSelf());
                waitingMoves++;
                log.info("waiting for " + waitingMoves + "moves to process moves");
                break;
            }
            case WAITING_PROCESSING:
            {
                buffer.add(addRoadPointMessage);
                log.info("Adding will be handled next time");
                break;
            }

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

    private void errorAddRoadPoint(ErrorAddRoadPointMessage errorAddRoadPointMessage) {
        waitingMoves--;
        log.info("waiting for " + waitingMoves + "moves to process moves");
        managerActor.tell(errorAddRoadPointMessage, getSelf());
        if (waitingMoves == 0) {
            sendMoves();
        }
    }

    private static enum Phase {
        COLLECTING_MOVES,
        WAITING_PROCESSING,
        WAITING_INITILIZATION
    }
}
