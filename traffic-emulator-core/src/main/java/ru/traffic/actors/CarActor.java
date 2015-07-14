package ru.traffic.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ru.traffic.car.Car;
import ru.traffic.messages.NextTimeMessage;
import ru.traffic.messages.manage.DeleteRoadPointMessage;
import ru.traffic.messages.move.MoveMessage;
import ru.traffic.messages.talk.AnsSkipMessage;
import ru.traffic.messages.talk.AskSkipMessage;
import ru.traffic.messages.talk.ChangeMoveMessage;
import ru.traffic.model.Move;
import ru.traffic.model.Position;
import ru.traffic.model.RoadPointInfo;
import ru.traffic.processing.InfoProcessor;
import ru.traffic.util.PositionUtil;
import ru.traffic.util.RoadArray;
import ru.traffic.util.RestorableStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Константин on 30.06.2015.
 */
public class CarActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final ActorRef decisionActor;

    private final Car car;

    private Position position;

    private Position futurePosition;

    private boolean alreadySkip;

    private RestorableStack<Move> moveStack;

    public CarActor(ActorRef decisionActor, Car car, Position position) {
        this.decisionActor = decisionActor;
        this.car = car;
        this.position = position;
        this.futurePosition = position;
        moveStack = new RestorableStack<>();
        alreadySkip = false;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof  NextTimeMessage) {
            nextTime((NextTimeMessage)o);
        } else  if (o instanceof ChangeMoveMessage) {
            handleChangeMoveMessage((ChangeMoveMessage)o);
        } else if (o instanceof AskSkipMessage) {
            handleAskSkipMessage((AskSkipMessage)o);
        } else if (o instanceof AnsSkipMessage) {
            handleAnsSkipMessage((AnsSkipMessage)o);
        } else {
            unhandled(o);
        }
    }

    private void fillMoveStack(RoadArray<RoadPointInfo> state) {
        moveStack.clear();
        boolean left = position.getLane() == 1;
        boolean right = position.getLane() == state.getLanesNumber();

        Integer leftFreeSpace = -1;
        Integer freeSpace = -1;
        Integer rightFreeSpace = -1;

        int wishLane = chooseWishLane(state);

        if (!left) leftFreeSpace = InfoProcessor.freeFrontSpace(state, PositionUtil.getLeft(position), car.wishSpeed());
        if (!right) rightFreeSpace = InfoProcessor.freeFrontSpace(state, PositionUtil.getRight(position), car.wishSpeed());
        freeSpace = InfoProcessor.freeFrontSpace(state, position, car.wishSpeed());

        //freeSpace is null means that a car is close to the edge of the road. No need to change line.
        if (leftFreeSpace == null || freeSpace == null || rightFreeSpace == null) {
            selfDelete();
            return;
        }

        for (int step = 0; step <= car.wishSpeed(); step++) {

            if (wishLane == 1) {
                if (step <= leftFreeSpace && step != 0 && position.getDistance() > 1) addMoveToStack(step, -1);
                if (step <= rightFreeSpace && step != 0 && position.getDistance() > 1) addMoveToStack(step, 1);
            } else {
                //todo optimize
                if (step <= rightFreeSpace && step != 0 && position.getDistance() > 1) addMoveToStack(step, 1);
                if (step <= leftFreeSpace && step != 0 && position.getDistance() > 1) addMoveToStack(step, -1);
            }

            if (step <= freeSpace) addMoveToStack(step, 0);
        }
    }

    private void addMoveToStack(int deltaDist, int deltaLane) {
        Position next = PositionUtil.move(position, deltaDist, deltaLane);
        moveStack.push(new Move(position, next));
    }

    private void selfDelete() {
        log.info("send self-delete request");
        DeleteRoadPointMessage deleteRoadPointMessage = new DeleteRoadPointMessage(position);
        decisionActor.tell(deleteRoadPointMessage, getSelf());
        getContext().stop(getSelf());
    }


    private void nextTime(NextTimeMessage nextTimeMessage) {
        alreadySkip = false;
        position = futurePosition;
        futurePosition = null;
        log.info("start thinking...");
        RoadArray<RoadPointInfo> state = nextTimeMessage.getState();
        fillMoveStack(state);
        doMoveFromStack();
    }

    private void doMoveFromStack() {
        Move move = moveStack.pop();
        log.info("doMove " + move);
        go(move);
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
                return speedDif != 0 ? speedDif/Math.abs(speedDif) : -1;
            }
            i++;
        }
        return -1;
    }

    private void handleChangeMoveMessage(ChangeMoveMessage changeMoveMessage) {
        if (!car.willAsk()) {
            doMoveFromStack();
        } else {
            Move wishMove = new Move(position, futurePosition);
            AskSkipMessage askSkipMessage = new AskSkipMessage(wishMove);
            log.info("Ask skip");
            changeMoveMessage.getCompetitor().tell(askSkipMessage, getSelf());
        }
    }

    private void handleAskSkipMessage(AskSkipMessage askSkipMessage) {
        moveStack.save();
        log.info("is asked for skip");
        if (!car.askSkip() || alreadySkip) {
            log.info("Answer: No (already skipping)");
            getSender().tell(new AnsSkipMessage(false), getSelf());
        } else if (moveStack.isEmpty()) {
            log.info("Answer: No (no move)");
            getSender().tell(new AnsSkipMessage(false), getSelf());
        } else {
            final Move competitorMove = askSkipMessage.getWishMove();
            Move myMove = moveStack.pop();
            while (conflict(myMove, competitorMove)) {
                if (!moveStack.empty()) {
                    myMove = moveStack.pop();
                } else {
                    log.info("Answer: No (no move)");
                    moveStack.restore();
                    getSender().tell(new AnsSkipMessage(false), getSelf());
                    return;
                }
            }
            log.info("Answer: yes");
            log.info("doMove " + myMove);
            go(myMove);
            getSender().tell(new AnsSkipMessage(true), getSelf());
        }
    }

    private void go(Move move) {
        futurePosition = move.getTo();
        decisionActor.tell(new MoveMessage(move), getSelf());
    }

    private boolean conflict(Move move1, Move move2) {
        Set<Position> positions = new HashSet<>();
        fillMove(positions, move1);
        return lookMove(positions, move2);
    }

    private void fillMove(Set<Position> positions, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
        positions.add(from);
        for (int i=0; i<=to.getDistance(); i++) {
            positions.add(new Position(from.getDistance() + i, to.getLane()));
        }
    }

    private boolean lookMove(Set<Position> positions, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
        if (positions.contains(from)) {
            return true;
        }
        for (int i=0; i<=to.getDistance(); i++) {
            if (positions.contains(new Position(from.getDistance() + i, to.getLane()))) {
                return true;
            }
        }
        return false;
    }


    private void handleAnsSkipMessage(AnsSkipMessage ansSkipMessage) {
        boolean willSkip = ansSkipMessage.isWillSkip();
        if (willSkip) {
            Move move = new Move(position, futurePosition);
            log.info("doMove " + move);
            go(move);
        } else {
            doMoveFromStack();
        }
    }
}
