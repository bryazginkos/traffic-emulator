package ru.traffic.util;

import ru.traffic.model.RoadPointInfo;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by Константин on 30.06.2015.
 */
public class RoadArray implements Cloneable {

    private final int length;
    private final int lanesNumber;
    private int elementsNum;

    private final RoadPointInfo[][] array;

    public RoadArray(int length, int lanesNumber) {
        this.length = length;
        this.lanesNumber = lanesNumber;
        array = new RoadPointInfo[length][lanesNumber];
        elementsNum = 0;
    }

    public RoadArray(RoadArray roadArray) {
        this.length = roadArray.getLength();
        this.lanesNumber = roadArray.getLanesNumber();
        this.elementsNum = roadArray.getElementsNum();
        array = new RoadPointInfo[length][lanesNumber];
        for (int i = 0; i < length; i ++) {
            array[i] = Arrays.copyOf(roadArray.array[i], lanesNumber);
        }
    }

    //todo check params
    public RoadPointInfo get (int distance, int lane) {
        return array[distance - 1][lane - 1];
    }

    //todo check params
    public void put(int distance, int lane, RoadPointInfo element) {
        RoadPointInfo old = array[distance -1][lane -1];
        if (old != null) {
            elementsNum--;
        }

        array[distance - 1][lane - 1] = element;
        if (element != null) {
            elementsNum++;
        }
    }

    public int getLength() {
        return length;
    }

    public int getLanesNumber() {
        return lanesNumber;
    }

    public int getElementsNum() {
        return elementsNum;
    }
}
