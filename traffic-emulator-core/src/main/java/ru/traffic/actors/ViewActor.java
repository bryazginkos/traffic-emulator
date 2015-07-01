package ru.traffic.actors;

import akka.actor.UntypedActor;
import ru.traffic.messages.NextTimeMessage;
import ru.traffic.util.RoadArray;

/**
 * Created by Константин on 01.07.2015.
 */
public class ViewActor extends UntypedActor {
    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof NextTimeMessage) {
            show((NextTimeMessage)o);
        } else {
            unhandled(o);
        }
    }

    private void show(NextTimeMessage nextTimeMessage) {
        RoadArray state = nextTimeMessage.getState();
        int lanes = state.getLanesNumber();
        int length = state.getLength();
        for (int i = 1; i <= lanes; i++) {
            for (int j=1; j<=length; j++) {
                System.out.print(state.get(j, i) != null ? "o" : "-");
            }
            System.out.println();
        }
    }
}
