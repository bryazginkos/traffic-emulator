package ru.traffic.messages.talk;

import akka.actor.ActorRef;

/**
 * Created by Константин on 09.07.2015.
 */
public class ChangeMoveMessage {

    private final ActorRef competitor;

    public ChangeMoveMessage(ActorRef competitor) {
        this.competitor = competitor;
    }

    public ActorRef getCompetitor() {
        return competitor;
    }
}
