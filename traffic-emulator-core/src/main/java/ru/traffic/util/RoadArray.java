package ru.traffic.util;

import java.lang.reflect.Array;

/**
 * Created by Константин on 30.06.2015.
 */
public class RoadArray<T> {

    private final int length;
    private final int lanesNumber;

    private final T[][] array;

    @SuppressWarnings("unchecked")
    public RoadArray(int length, int lanesNumber, Class<T> tClass) {
        this.length = length;
        this.lanesNumber = lanesNumber;
        array = (T[][]) Array.newInstance(tClass, length, lanesNumber);
    }

    //todo check params
    public T get (int distance, int lane) {
        return array[distance - 1][lane - 1];
    }

    //todo check params
    public void put(int distance, int lane, T element) {
        array[distance - 1][lane - 1] = element;
    }


}
