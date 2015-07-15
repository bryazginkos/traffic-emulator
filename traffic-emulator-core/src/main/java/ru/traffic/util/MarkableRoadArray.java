package ru.traffic.util;

import java.util.function.Predicate;

/**
 * Created by Константин on 15.07.2015.
 */
public class MarkableRoadArray<T>  {

    private RoadArray<Boolean> markers;

    private RoadArray<T> data;

    public MarkableRoadArray(int length, int lanesNumber, Class<T> tClass) {
        markers = new RoadArray<>(length, lanesNumber, Boolean.class);
        data = new RoadArray<>(length, lanesNumber, tClass);
    }

    public T get(int distance, int lane) {
        return data.get(distance, lane);
    }

    public void putWithCondition(int distance, int lane, T element, Predicate<T> predicate) {
        data.putWithCondition(distance, lane, element, predicate);
    }

    public int getLength() {
        return data.getLength();
    }

    public void put(int distance, int lane, T element) {
        data.put(distance, lane, element);
    }

    public int getLanesNumber() {
        return data.getLanesNumber();
    }

    public void clear() {
        data.clear();
    }

    public int getElementsNum() {
        return data.getElementsNum();
    }

    public boolean isMarked(int distance, int lane) {
        return markers.get(distance, lane) == Boolean.TRUE;
    }

    public void markAll() {
        for (int lane = 1; lane <= data.getLanesNumber(); lane++) {
            for (int distance = 1; distance <= data.getLength(); distance++) {
                if (data.get(distance, lane) != null) {
                    markers.put(distance, lane, Boolean.TRUE);
                }
            }
        }
    }
}
