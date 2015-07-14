package ru.traffic.util;

import com.google.common.base.Objects;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Created by ���������� on 30.06.2015.
 */
public class RoadArray<T> {

    private final int length;
    private final int lanesNumber;
    private int elementsNum;

    private final T[][] array;

    public RoadArray(int length, int lanesNumber, Class<T> tClass) {
        this.length = length;
        this.lanesNumber = lanesNumber;
        array = (T[][]) Array.newInstance(tClass, length, lanesNumber);
        elementsNum = 0;
    }

    public RoadArray(RoadArray<T> roadArray, Class<T> tClass) {
        this.length = roadArray.getLength();
        this.lanesNumber = roadArray.getLanesNumber();
        this.elementsNum = roadArray.getElementsNum();
        array = (T[][]) Array.newInstance(tClass, length, lanesNumber);
        for (int i = 0; i < length; i ++) {
            array[i] = Arrays.copyOf(roadArray.array[i], lanesNumber);
        }
    }

    //todo check params
    public T get (int distance, int lane) {
        return array[distance - 1][lane - 1];
    }

    //todo check params
    public void put(int distance, int lane, T element) {
        T old = array[distance -1][lane -1];
        if (old != null) {
            elementsNum--;
        }

        array[distance - 1][lane - 1] = element;
        if (element != null) {
            elementsNum++;
        }
    }

    public void putWithCondition(int distance, int lane, T element, Predicate<T> predicate) {
        T oldElement = get(distance, lane);
        if (predicate.test(oldElement)) {
            put(distance, lane, element);
        }
    }

    public void clear() {
        elementsNum = 0;
        for (int i = 0; i < length; i ++) {
            Arrays.setAll(array[i], t -> null);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoadArray)) return false;
        RoadArray<?> roadArray = (RoadArray<?>) o;
        return Objects.equal(length, roadArray.length) &&
                Objects.equal(lanesNumber, roadArray.lanesNumber) &&
                Objects.equal(elementsNum, roadArray.elementsNum) &&
                Objects.equal(array, roadArray.array);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(length, lanesNumber, elementsNum, array);
    }
}
