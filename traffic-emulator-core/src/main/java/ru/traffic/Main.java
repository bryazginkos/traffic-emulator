package ru.traffic;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import ru.traffic.actors.ManagerActor;
import ru.traffic.car.CarImpl;
import ru.traffic.car.SlowFrontAction;
import ru.traffic.car.StopFrontAction;
import ru.traffic.messages.manage.AddRoadPointClientMessage;
import ru.traffic.messages.manage.InitMessage;
import ru.traffic.model.Position;

/**
 * Created by Константин on 01.07.2015.
 */
public class Main {
    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("traffic");
        ActorRef manager = actorSystem.actorOf(Props.create(ManagerActor.class), "manager");

        InitMessage initMessage = new InitMessage(3, 50);
        manager.tell(initMessage, ActorRef.noSender());

        Position initPosition = new Position(1,1);
        AddRoadPointClientMessage clientMessage = new AddRoadPointClientMessage(initPosition, new CarImpl(0,5, SlowFrontAction.SLOW, StopFrontAction.STOP));

        manager.tell(clientMessage, ActorRef.noSender());
    }
}
