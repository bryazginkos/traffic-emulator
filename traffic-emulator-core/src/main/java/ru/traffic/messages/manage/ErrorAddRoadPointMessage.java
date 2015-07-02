package ru.traffic.messages.manage;

import akka.actor.ActorRef;

/**
 * Created by Константин on 02.07.2015.
 */
public class ErrorAddRoadPointMessage {
    private ActorRef actorRef;

    public ErrorAddRoadPointMessage(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }
}
