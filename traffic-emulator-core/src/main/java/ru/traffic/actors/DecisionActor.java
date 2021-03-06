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
import ru.traffic.messages.talk.ChangeMoveMessage;
import ru.traffic.model.Move;
import ru.traffic.model.Position;
import ru.traffic.model.RoadPointInfo;
import ru.traffic.util.MarkableRoadArray;
import ru.traffic.util.RoadArray;

import java.util.*;

/**
 * Created by ���������� on 30.06.2015.
 */
public class DecisionActor extends UntypedActor {

    private final static int MAX_SPEED = 3;

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ActorRef roadActor;

    private ActorRef managerActor;

    private int waitingMoves;

    private Map<ActorRef, Move> movesMap;

    private MarkableRoadArray<ActorRef> pointsMap;

    private List<AddRoadPointMessage> buffer;

    private Phase phase;

    public DecisionActor(ActorRef roadActor, ActorRef managerActor, int lanes, int length) {
        this.roadActor = roadActor;
        this.managerActor = managerActor;
        movesMap = new HashMap<>();
        buffer = new ArrayList<>();
        phase = Phase.WAITING_INITILIZATION;
        pointsMap = new MarkableRoadArray<>(length, lanes, ActorRef.class);
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
        resetPointsMap(nextTimeMessage.getState());
        phase = Phase.COLLECTING_MOVES;
        log.info("nextTime (waiting for " + nextTimeMessage.getState().getElementsNum() +" moves and  + " + buffer.size() + " moves will add from new points)");
        waitingMoves = nextTimeMessage.getState().getElementsNum();
        buffer.forEach(addRoadPointMessage -> addRoadPoint(addRoadPointMessage));
        buffer.clear();
        movesMap.entrySet().forEach(entry -> entry.getKey().tell(nextTimeMessage, getSelf()));
        movesMap = new HashMap<>(waitingMoves);
        phase = Phase.COLLECTING_MOVES;
    }

    private void takeMove(MoveMessage moveMessage) throws Exception {
        if (waitingMoves == 0) {
            throw new Exception(getSender().toString());
        }
        log.info("take move decision move=" + moveMessage.getMove());
        Set<ActorRef> competitors = tryRegisterMove(moveMessage.getMove(), getSender());
        if (competitors != null) {
            assert !competitors.isEmpty();
            if (moveMessage.getMove().getFrom().getLane() == moveMessage.getMove().getTo().getLane()) {
                waitingMoves = waitingMoves + competitors.size();
                log.info("rewrite move");
                competitors.stream().forEach(this::removeMoveIfExist);
                competitors.stream().forEach(movesMap::remove);
                competitors.stream().forEach(competitor -> competitor.tell(new ChangeMoveMessage(getSender()), getSelf()));
            } else {
                log.info("move is impossible");
                if (movesMap.containsKey(getSender())) {
                    waitingMoves++;
                    movesMap.remove(getSender());
                }
                getSender().tell(new ChangeMoveMessage(competitors.stream().findFirst().get()), getSelf());
                return;
            }
        }
        if (!movesMap.containsKey(getSender())) {
            waitingMoves--;
        } else {
            log.info("change move decision");
        }
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
                boolean allow = tryAddToPointsMap(distance, lane, roadPointInfo.getActorRef());
                if (allow) {
                    roadActor.tell(addRoadPointMessage, getSelf());
                    waitingMoves++;
                    log.info("waiting for " + waitingMoves + "moves to process moves");
                } else {
                    log.info("Can't put point in to distance=" + distance + " lane=" + lane);
                    getContext().stop(addRoadPointMessage.getRoadPointInfo().getActorRef());
                    managerActor.tell(new ErrorAddRoadPointMessage(addRoadPointMessage.getRoadPointInfo().getActorRef()), getSelf());
                }
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
        if (waitingMoves < 0) {
            log.error("waiting moves is negative");
        }
        log.info("waiting for " + waitingMoves + "moves to process moves");
        if (waitingMoves == 0) {
            sendMoves();
        }
    }

    private void resetPointsMap(RoadArray<RoadPointInfo> state) {
        pointsMap.clear();
        for (int distance = 1; distance <= state.getLength(); distance++) {
            for (int lane = 1; lane <= state.getLanesNumber(); lane++) {
                RoadPointInfo roadPointInfo = state.get(distance, lane);
                if (roadPointInfo != null) {
                    pointsMap.put(distance, lane, roadPointInfo.getActorRef());
                }
            }
        }
        pointsMap.markAll();
    }

    private Set<ActorRef> tryRegisterMove(Move move, ActorRef actorRef) {
        removeMoveIfExist(actorRef, move.getFrom());
        int distanceTo = move.getTo().getDistance();
        int distanceFrom = move.getFrom().getDistance();
        int laneTo = move.getTo().getLane();
        int laneFrom = move.getFrom().getLane();

        int deltaDistance = distanceTo - distanceFrom;

        assert pointsMap.get(distanceFrom, laneFrom) == actorRef;

        Set<ActorRef> competitors = new HashSet<>();

        for (int i = 1; i <= deltaDistance; i++) {
            ActorRef competitorNeighbour = pointsMap.get(distanceFrom + i, laneTo);
            if (competitorNeighbour != null) competitors.add(competitorNeighbour);
        }
        if (laneTo != laneFrom) {
            ActorRef competitorNeighbour = pointsMap.get(distanceFrom, laneTo);
            if (competitorNeighbour != null) competitors.add(competitorNeighbour);
        }

        if (!competitors.isEmpty()) {
            return competitors;
        }

        for (int i = 1; i <= deltaDistance; i++) {
            pointsMap.put(distanceFrom + i, laneTo, actorRef);
        }
        if (laneFrom != laneTo) {
            pointsMap.put(distanceFrom, laneFrom, actorRef);
        }
        return null;
    }

    private void removeMoveIfExist(ActorRef actorRef, Position fromPosition) {
        int distanceFrom = fromPosition.getDistance();
        int laneFrom = fromPosition.getLane();
        boolean left = laneFrom == 1;
        boolean right = laneFrom == pointsMap.getLanesNumber();
        int i = 0;
        while (i <= MAX_SPEED && distanceFrom + i <= pointsMap.getLength()) {
            if (!left) pointsMap.putWithCondition(distanceFrom + i, laneFrom - 1, null, oldAct -> oldAct == actorRef);
            if (!right) pointsMap.putWithCondition(distanceFrom + i, laneFrom + 1, null, oldAct -> oldAct == actorRef);
            if (i != 0) pointsMap.putWithCondition(distanceFrom + i, laneFrom, null, oldAct -> oldAct == actorRef);
            i++;
        }
    }

    private void removeMoveIfExist(ActorRef actorRef) {
        //todo possible optimize if look from one point
        for (int distance = 1; distance <= pointsMap.getLength(); distance++) {
            for (int lane = 1; lane <= pointsMap.getLanesNumber(); lane++) {
                if (!pointsMap.isMarked(distance, lane)) {
                    pointsMap.putWithCondition(distance, lane, null, oldAct -> oldAct == actorRef);
                }
            }
        }
    }

    private boolean tryAddToPointsMap(int distance, int lane, ActorRef actorRef) {
        if (pointsMap.get(distance, lane) != null) {
            return false;
        }
        pointsMap.put(distance, lane, actorRef);
        return true;
    }

    private enum Phase {
        COLLECTING_MOVES,
        WAITING_PROCESSING,
        WAITING_INITILIZATION
    }
}
