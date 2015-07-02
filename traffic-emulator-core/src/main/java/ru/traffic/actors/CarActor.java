package ru.traffic.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ru.traffic.car.Car;
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

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final ActorRef decisionActor;

    private final Car car;

    private Position position;

    public CarActor(ActorRef decisionActor, Car car, Position position) {
        this.decisionActor = decisionActor;
        this.car = car;
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
        log.info("start Thinking...");
        RoadArray state = nextTimeMessage.getState();
        try {
            int freeSpace = InfoProcessor.freeFrontSpace(state, position, car.wishSpeed());
            //todo use car class to get behaviour
            Position futurePosition = new Position(position.getDistance() + freeSpace, position.getLane());
            Move move = new Move(position, futurePosition);
            position = futurePosition;
            log.info("Send move");
            decisionActor.tell(new MoveMessage(move), getSelf());
        } catch (OutOfViewException e) {
            log.info("send self-delete request");
            DeleteRoadPointMessage deleteRoadPointMessage = new DeleteRoadPointMessage(position);
            decisionActor.tell(deleteRoadPointMessage, getSelf());
            getContext().stop(getSelf());
        }

    }
}
