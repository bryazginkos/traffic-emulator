package ru.traffic.actors;

import akka.actor.UntypedActor;
import ru.traffic.messages.NextTimeMessage;
import ru.traffic.util.RoadArray;

import java.util.function.Consumer;

/**
 * Created by Константин on 01.07.2015.
 */
public class ViewActor extends UntypedActor {

    private Consumer<RoadArray> consumer;

    public ViewActor(Consumer<RoadArray> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof NextTimeMessage) {
            show((NextTimeMessage)o);
        } else {
            unhandled(o);
        }
    }

    private void show(NextTimeMessage nextTimeMessage) {
        consumer.accept(nextTimeMessage.getState());
    }
}
