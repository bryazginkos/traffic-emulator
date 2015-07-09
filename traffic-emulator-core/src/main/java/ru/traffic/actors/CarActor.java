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
import ru.traffic.model.RoadPointInfo;
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

    private Position futurePosition;

    private int freeSpace;

    public CarActor(ActorRef decisionActor, Car car, Position position) {
        this.decisionActor = decisionActor;
        this.car = car;
        this.position = position;
        this.futurePosition = position;
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
        position = futurePosition;
        futurePosition = null;
        log.info("start thinking...");
        RoadArray<RoadPointInfo> state = nextTimeMessage.getState();
        try {
            freeSpace = InfoProcessor.freeFrontSpace(state, position, car.wishSpeed());
            if (freeSpace >= car.wishSpeed()) {
                goStraight(car.wishSpeed());
            } else if (freeSpace > 0) {
                switch (car.slowFront()) {
                    case SIDE: {
                        wantChangeLane(car.wishSpeed(), state);
                        break;
                    }
                    case SLOW: {
                        goStraight(freeSpace);
                        break;
                    }
                }
            } else {
                assert  freeSpace == 0;
                switch (car.stopFront()) {
                    case SIDE: {
                        wantChangeLane(0, state);
                        break;
                    }
                    case STOP: {
                        goStraight(0);
                        break;
                    }
                }
            }

        } catch (OutOfViewException e) {
            log.info("send self-delete request");
            DeleteRoadPointMessage deleteRoadPointMessage = new DeleteRoadPointMessage(position);
            decisionActor.tell(deleteRoadPointMessage, getSelf());
            getContext().stop(getSelf());
        }

    }

    private void goStraight(int step) {
        futurePosition = new Position(position.getDistance() + step, position.getLane());
        Move move = new Move(position, futurePosition);
        log.info("Send move (go straight " + step + " steps)");
        decisionActor.tell(new MoveMessage(move), getSelf());
    }

    private void wantChangeLane(int speed, RoadArray<RoadPointInfo> state) {
        if (position.getDistance() == 1) {
            goStraight(freeSpace);
        }
        int laneAdd = chooseWishLane(state);
        futurePosition = new Position(position.getDistance() + speed, position.getLane() + laneAdd);
        Move move = new Move(position, futurePosition);
        log.info("Send move (go straight " + speed + " steps, laneAdd=" + laneAdd + ")");
        decisionActor.tell(new MoveMessage(move), getSelf());
    }

    private int chooseWishLane(RoadArray<RoadPointInfo> state) {
        if (state.getLanesNumber() == 1) {
            return 0;
        }
        if (position.getLane() == 1) {
            return 1;
        }
        if (position.getLane() == state.getLanesNumber()) {
            return  -1;
        }
        int i = 0;

        while (i <= state.getLength() - position.getDistance()) {
            int viewDistance = position.getDistance() + i;
            RoadPointInfo right = state.get(viewDistance, position.getLane() + 1);
            RoadPointInfo left =  state.get(viewDistance, position.getLane() - 1);
            if (right == null && left != null) {
                return 1;
            }
            if (right != null && left == null) {
                return -1;
            }
            if (left != null && right != null) {
                int speedDif = right.getSpeed() - left.getSpeed();
                return speedDif/Math.abs(speedDif);
            }
            i++;
        }
        return -1;

    }
}
