package ru.traffic.util;

import ru.traffic.model.RoadPointInfo;

import java.lang.reflect.Array;

/**
 * Created by Константин on 30.06.2015.
 */
public class RoadArray {

    private final int length;
    private final int lanesNumber;

    private final RoadPointInfo[][] array;

    @SuppressWarnings("unchecked")
    public RoadArray(int length, int lanesNumber) {
        this.length = length;
        this.lanesNumber = lanesNumber;
        array = (RoadPointInfo[][]) Array.newInstance(RoadPointInfo.class, length, lanesNumber);
    }

    //todo check params
    public RoadPointInfo get (int distance, int lane) {
        return array[distance - 1][lane - 1];
    }

    //todo check params
    public void put(int distance, int lane, RoadPointInfo element) {
        array[distance - 1][lane - 1] = element;
    }

    public int getLength() {
        return length;
    }

    public int getLanesNumber() {
        return lanesNumber;
    }
}
