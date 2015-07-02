package ru.traffic.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ru.traffic.exception.AccidentException;
import ru.traffic.exception.OccupiedPlaceException;
import ru.traffic.messages.NextTimeMessage;
import ru.traffic.messages.manage.AddRoadPointMessage;
import ru.traffic.messages.manage.DeleteRoadPointMessage;
import ru.traffic.messages.manage.InitMessage;
import ru.traffic.messages.move.MovesMessage;
import ru.traffic.model.RoadPointInfo;
import ru.traffic.processing.MoveProcessor;
import ru.traffic.util.RoadArray;

/**
 * Created by Константин on 30.06.2015.
 */
public class RoadActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private RoadArray roadArray;
    private ActorRef viewActor;

    public RoadActor(ActorRef viewActor) {
        this.viewActor = viewActor;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof InitMessage) {
            init((InitMessage)o);
        } else if (o instanceof AddRoadPointMessage) {
            addRoadPoint((AddRoadPointMessage)o);
        } else if (o instanceof MovesMessage) {
            doMoves((MovesMessage)o);
        } else  if (o instanceof DeleteRoadPointMessage) {
            deleteRoadPoint((DeleteRoadPointMessage)o);
        }  else {
            unhandled(o);
        }
    }

    private void init(InitMessage initMessage) {
        log.info("Create Road: lanes=" + initMessage.getLanes() + " length=" + initMessage.getLength());
        int lanes = initMessage.getLanes();
        int length = initMessage.getLength();
        roadArray = new RoadArray(length, lanes);
    }

    private void addRoadPoint(AddRoadPointMessage addRoadPointMessage) throws OccupiedPlaceException {
        int distance = addRoadPointMessage.getDistance();
        int lane = addRoadPointMessage.getLane();
        RoadPointInfo roadPointInfo = addRoadPointMessage.getRoadPointInfo();
        //todo check initilization and params
        if (roadArray.get(distance, lane) != null) {
            log.info("Can't put point in to disntace=" + distance + " lane=" + lane);
            throw new OccupiedPlaceException();
        }
        log.info("Put point into array: distance=" + distance + " lane=" + lane);
        roadArray.put(distance, lane, roadPointInfo);
        NextTimeMessage nextTimeMessage = new NextTimeMessage(roadArray);
        roadPointInfo.getActorRef().tell(nextTimeMessage, getSelf());
    }

    private void doMoves(MovesMessage movesMessage) throws AccidentException, InterruptedException {
        //todo check initilization
        Thread.sleep(1000);
        log.info("processing moves...");
        MoveProcessor.processing(roadArray, movesMessage.getMoves());
        log.info("processing moves has been succesfully finished");
        NextTimeMessage nextTimeMessage = new NextTimeMessage(roadArray);
        getSender().tell(nextTimeMessage, getSelf());
        viewActor.tell(nextTimeMessage, getSelf());
    }

    private void deleteRoadPoint(DeleteRoadPointMessage deleteRoadPointMessage) {
        //todo check initilization
        log.info("DeleteRoadPoint position=" + deleteRoadPointMessage.getPosition());
        int distance = deleteRoadPointMessage.getPosition().getDistance();
        int lane = deleteRoadPointMessage.getPosition().getLane();
        roadArray.put(distance, lane, null);
    }
}
