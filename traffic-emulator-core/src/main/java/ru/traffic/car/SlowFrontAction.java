package ru.traffic.car;

import java.util.Arrays;

/**
 * Created by Константин on 01.07.2015.
 */
public enum SlowFrontAction {
    SLOW("slow"),
    SIDE("side");

    private String id;

    SlowFrontAction(String id) {
        this.id = id;
    }

    public static SlowFrontAction getById(String id) {
        return Arrays.stream(SlowFrontAction.values())
                .filter(slowFrontAction -> slowFrontAction.id.equals(id))
                .findAny()
                .get();
    }
}
