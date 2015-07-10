package ru.traffic.messages.talk;

import akka.actor.ActorRef;

/**
 * Created by Константин on 09.07.2015.
 */
public class ChangeMoveMessage {

    private final boolean necessarily;

    private final ActorRef competitor;

    public ChangeMoveMessage(boolean necessarily, ActorRef competitor) {
        this.necessarily = necessarily;
        this.competitor = competitor;
    }

    public boolean isNecessarily() {
        return necessarily;
    }

    public ActorRef getCompetitor() {
        return competitor;
    }
}
