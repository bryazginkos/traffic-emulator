package ru.traffic.view;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.traffic.actors.ManagerActor;
import ru.traffic.car.Car;
import ru.traffic.car.CarImpl;
import ru.traffic.car.SlowFrontAction;
import ru.traffic.car.StopFrontAction;
import ru.traffic.messages.manage.AddRoadPointClientMessage;
import ru.traffic.messages.manage.InitMessage;
import ru.traffic.model.Position;
import ru.traffic.util.RoadArray;
import ru.traffic.view.json.InitParameters;
import ru.traffic.view.json.RoadPoint;
import ru.traffic.view.util.RoadTransformer;

import java.util.function.Consumer;

/**
 * Created by Константин on 01.07.2015.
 */
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RoadTransformer roadTransformer;

    private ActorRef manager;

    @MessageMapping("/createroad")
    public void createRoad(InitParameters initParameters) throws Exception {

        Consumer<RoadArray> viewConsumer = roadArray -> sendResult(roadArray);

        ActorSystem actorSystem = ActorSystem.create("traffic");
        manager = actorSystem.actorOf(Props.create(ManagerActor.class, viewConsumer), "manager");
        //todo check params
        InitMessage initMessage = new InitMessage(initParameters.getLanes(), initParameters.getLength());
        manager.tell(initMessage, ActorRef.noSender());
    }

    @MessageMapping("/createpoint")
    public void createPoint(RoadPoint roadPoint) {
        Position position = new Position(roadPoint.getDistance(), roadPoint.getLane());
        Car car = new CarImpl(roadPoint.getPoliteness(), roadPoint.getWishspeed(),
                SlowFrontAction.getById(roadPoint.getSlowFrontAction()), StopFrontAction.getById(roadPoint.getStopFrontAction()));
        AddRoadPointClientMessage addRoadPointMessage = new AddRoadPointClientMessage(position, car);
        manager.tell(addRoadPointMessage, ActorRef.noSender());
    }

    private void sendResult(RoadArray roadArray) {
        messagingTemplate.convertAndSend("/topic/to", roadTransformer.convert(roadArray));
    }

}
