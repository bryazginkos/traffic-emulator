package ru.traffic.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import ru.traffic.exception.OutOfViewException;
import ru.traffic.messages.NextTimeMessage;
import ru.traffic.messages.manage.DeleteRoadPointMessage;
import ru.traffic.messages.move.MoveMessage;
import ru.traffic.model.Move;
import ru.traffic.model.Position;
import ru.traffic.processing.InfoProcessor;
import ru.traffic.util.RoadArray;

/**
 * Created by Константин on 30.06.2015.
 */
public class CarActor extends UntypedActor {

    private ActorRef decisionActor;

    int wishSpeed;

    private Position position;

    public CarActor(ActorRef decisionActor, int wishSpeed, Position position) {
        this.decisionActor = decisionActor;
        this.wishSpeed = wishSpeed;
        this.position = position;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof  NextTimeMessage) {
            nextTime((NextTimeMessage)o);
        } else {
            unhandled(o);
        }
    }

    private void nextTime(NextTimeMessage nextTimeMessage) {
        RoadArray state = nextTimeMessage.getState();
        try {
            int freeSpace = InfoProcessor.freeFrontSpace(state, position, wishSpeed);
            //todo logic must be out of class
            Position futurePosition = new Position(position.getDistance() + freeSpace, position.getLane());
            Move move = new Move(position, futurePosition);
            position = futurePosition;
            decisionActor.tell(new MoveMessage(move), getSelf());
        } catch (OutOfViewException e) {
            DeleteRoadPointMessage deleteRoadPointMessage = new DeleteRoadPointMessage(position);
            decisionActor.tell(deleteRoadPointMessage, getSelf());
        }

    }
}
