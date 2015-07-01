package ru.traffic.car;

import java.util.Arrays;

/**
 * Created by Константин on 01.07.2015.
 */
public enum  StopFrontAction {
    STOP("stop"),
    SIDE("side");

    private String id;

    StopFrontAction(String id) {
        this.id = id;
    }

    public static StopFrontAction getById(String id) {
        return Arrays.stream(StopFrontAction.values())
                .filter(stopFrontAction -> stopFrontAction.id.equals(id))
                .findAny()
                .get();
    }
}
