package ru.traffic.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import ru.traffic.car.Car;
import ru.traffic.messages.manage.AddRoadPointClientMessage;
import ru.traffic.messages.manage.AddRoadPointMessage;
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
        } else {
            unhandled(o);
        }
    }

    private void init(InitMessage initMessage) {
        roadActor = getContext().actorOf(Props.create(RoadActor.class, viewActor), "road");
        decisionActor = getContext().actorOf(Props.create(DecisionActor.class, roadActor), "decision");
        roadPointActors = new HashSet<>();
        roadActor.tell(initMessage, getSelf());
    }

    private void addRoadPoint(AddRoadPointClientMessage addRoadPointClientMessage) {
        Position position = addRoadPointClientMessage.getPosition();
        Car car = addRoadPointClientMessage.getCar();
        //todo generate name?
        ActorRef roadPoint = getContext().actorOf(Props.create(CarActor.class, decisionActor, car, position));
        RoadPointInfo roadPointInfo = new RoadPointInfo(car.wishSpeed(), roadPoint);
        AddRoadPointMessage addRoadPointMessage = new AddRoadPointMessage(position.getDistance(), position.getLane(), roadPointInfo);
        roadPointActors.add(roadPoint);
        decisionActor.tell(addRoadPointMessage, roadPoint);
    }
}
