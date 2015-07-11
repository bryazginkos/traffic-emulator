package ru.traffic.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ru.traffic.messages.NextTimeMessage;
import ru.traffic.model.RoadPointInfo;
import ru.traffic.util.RoadArray;

import java.util.function.Consumer;

/**
 * Created by Константин on 01.07.2015.
 */
public class ViewActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private Consumer<RoadArray<RoadPointInfo>> consumer;

    public ViewActor(Consumer<RoadArray<RoadPointInfo>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof NextTimeMessage) {
            log.info("publish road");
            show((NextTimeMessage)o);
        } else {
            unhandled(o);
        }
    }

    private void show(NextTimeMessage nextTimeMessage) {
        consumer.accept(nextTimeMessage.getState());
    }
}
