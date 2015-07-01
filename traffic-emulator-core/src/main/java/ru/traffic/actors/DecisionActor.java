package ru.traffic.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
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
        waitingMoves = nextTimeMessage.getState().getElementsNum();
        movesMap.entrySet().forEach(entry -> entry.getKey().tell(nextTimeMessage, getSelf()));
        movesMap = new HashMap<>(waitingMoves);
    }

    private void takeMove(MoveMessage moveMessage) {
        waitingMoves--;
        movesMap.put(getSender(), moveMessage.getMove());
        if (waitingMoves == 0) {
            sendMoves();
        }
    }

    private void sendMoves() {
        MovesMessage movesMessage = new MovesMessage(movesMap.values());
        roadActor.tell(movesMessage, getSelf());
    }

    private void addRoadPoint(AddRoadPointMessage addRoadPointMessage) {
        roadActor.tell(addRoadPointMessage, getSender());
        int distance = addRoadPointMessage.getDistance();
        int lane = addRoadPointMessage.getLane();
        Position position = new Position(distance, lane);
        Move move = new Move(position, position);
        //to catch accident
        movesMap.put(getSender(), move);
        if (waitingMoves == 0) {
            sendMoves();
        }
    }

    private void deleteRoadPoint(DeleteRoadPointMessage deleteRoadPointMessage) {
        roadActor.tell(deleteRoadPointMessage, getSender());
        movesMap.remove(getSender());
        waitingMoves--;
        if (waitingMoves == 0) {
            sendMoves();
        }
    }
}
