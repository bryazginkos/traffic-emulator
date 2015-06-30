package ru.traffic.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Константин on 30.06.2015.
 */
public class RoadArrayTest {

    private static final int LENGTH = 12;
    private static final int LANES_NUMBER = 4;
    private RoadArray<String> roadArray;

    @Before
    public void initialization() {
        roadArray = new RoadArray<String>(LENGTH, LANES_NUMBER, String.class);
    }

    @Test
    public void testAddElement() {
        roadArray.put(3, 2, "hello");
    }

    @Test
    public void testAddElementEnd() {
        roadArray.put(LENGTH, 2, "hello");
    }

    @Test
    public void testAddElementBegin() {
        roadArray.put(1, 2, "hello");
    }

    @Test
    public void testAddElementLeft() {
        roadArray.put(3, 1, "hello");
    }

    @Test
    public void testAddElementRight() {
        roadArray.put(3, LANES_NUMBER, "hello");
    }

    @Test
    public void testGetElement() {
        String str = "hello";
        roadArray.put(3, 3, str);
        String takeStr = roadArray.get(3, 3);
        Assert.assertEquals(str, takeStr);
    }
}
