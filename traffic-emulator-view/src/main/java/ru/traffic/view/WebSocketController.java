package ru.traffic.view;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.traffic.actors.ManagerActor;
import ru.traffic.messages.manage.AddRoadPointClientMessage;
import ru.traffic.messages.manage.InitMessage;
import ru.traffic.model.Position;
import ru.traffic.util.RoadArray;
import ru.traffic.view.json.InitParameters;
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

    @MessageMapping("/websocketservice")
    public void startProcessing(InitParameters initParameters) throws Exception {

        Consumer<RoadArray> viewConsumer = roadArray -> sendResult(roadArray);

        ActorSystem actorSystem = ActorSystem.create("traffic");
        ActorRef manager = actorSystem.actorOf(Props.create(ManagerActor.class, viewConsumer), "manager");
        //todo check params
        InitMessage initMessage = new InitMessage(initParameters.getLanes(), initParameters.getLength());
        manager.tell(initMessage, ActorRef.noSender());

        //todo new request
        Position initPosition = new Position(1,1);
        AddRoadPointClientMessage clientMessage = new AddRoadPointClientMessage(initPosition, 1);

        manager.tell(clientMessage, ActorRef.noSender());
    }

    private void sendResult(RoadArray roadArray) {
        messagingTemplate.convertAndSend("/topic/to", roadTransformer.convert(roadArray));
    }

}
