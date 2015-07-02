package ru.traffic.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ru.traffic.car.Car;
import ru.traffic.messages.manage.AddRoadPointClientMessage;
import ru.traffic.messages.manage.AddRoadPointMessage;
import ru.traffic.messages.manage.ErrorAddRoadPointMessage;
import ru.traffic.messages.manage.InitMessage;
import ru.traffic.model.Position;
import ru.traffic.model.RoadPointInfo;
import ru.traffic.util.RoadArray;

import java.util.HashSet;
import java.util.function.Consumer;

/**
 * Created by Константин on 01.07.2015.
 */
public class ManagerActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ActorRef decisionActor;
    private ActorRef roadActor;
    private ActorRef viewActor;
    private HashSet<ActorRef> roadPointActors;

    public ManagerActor(Consumer<RoadArray> consumer) {
        viewActor = getContext().actorOf(Props.create(ViewActor.class, consumer), "view");
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof InitMessage) {
            init((InitMessage)o);
        } else if (o instanceof AddRoadPointClientMessage) {
            addRoadPoint((AddRoadPointClientMessage)o);
        } else if (o instanceof ErrorAddRoadPointMessage) {
            errorAddRoadPoint((ErrorAddRoadPointMessage)o);
        } else {
            unhandled(o);
        }
    }

    private void init(InitMessage initMessage) {
        log.info("Create actors for new road: lanes=" + initMessage.getLanes() + " length=" + initMessage.getLength());
        roadActor = getContext().actorOf(Props.create(RoadActor.class, viewActor), "road");
        decisionActor = getContext().actorOf(Props.create(DecisionActor.class, roadActor, getSelf()), "decision");
        roadPointActors = new HashSet<>();
        roadActor.tell(initMessage, getSelf());
    }

    private void addRoadPoint(AddRoadPointClientMessage addRoadPointClientMessage) {
        log.info("Create roadPoint: position=" + addRoadPointClientMessage.getPosition() + " car=" +  addRoadPointClientMessage.getCar());
        Position position = addRoadPointClientMessage.getPosition();
        Car car = addRoadPointClientMessage.getCar();
        //todo generate name?
        ActorRef roadPoint = getContext().actorOf(Props.create(CarActor.class, decisionActor, car, position));
        RoadPointInfo roadPointInfo = new RoadPointInfo(car.wishSpeed(), roadPoint);
        AddRoadPointMessage addRoadPointMessage = new AddRoadPointMessage(position.getDistance(), position.getLane(), roadPointInfo);
        roadPointActors.add(roadPoint);
        decisionActor.tell(addRoadPointMessage, roadPoint);
    }

    private void errorAddRoadPoint(ErrorAddRoadPointMessage errorAddRoadPointMessage) {
        log.info("remove error actor from set");
        roadPointActors.remove(errorAddRoadPointMessage.getActorRef());
    }
}
