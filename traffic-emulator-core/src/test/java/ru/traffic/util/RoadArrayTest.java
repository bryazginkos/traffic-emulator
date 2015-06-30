package ru.traffic.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.traffic.model.RoadPointInfo;

/**
 * Created by Константин on 30.06.2015.
 */
public class RoadArrayTest {

    private static final int LENGTH = 12;
    private static final int LANES_NUMBER = 4;
    private RoadArray roadArray;
    private RoadPointInfo roadPointInfo;

    @Before
    public void initialization() {
        roadArray = new RoadArray(LENGTH, LANES_NUMBER);
        roadPointInfo = new RoadPointInfo(12, null);
    }

    @Test
    public void testAddElement() {
        roadArray.put(3, 2, roadPointInfo);
    }

    @Test
    public void testAddElementEnd() {
        roadArray.put(LENGTH, 2, roadPointInfo);
    }

    @Test
    public void testAddElementBegin() {
        roadArray.put(1, 2, roadPointInfo);
    }

    @Test
    public void testAddElementLeft() {
        roadArray.put(3, 1, roadPointInfo);
    }

    @Test
    public void testAddElementRight() {
        roadArray.put(3, LANES_NUMBER, roadPointInfo);
    }

    @Test
    public void testGetElement() {
        roadArray.put(3, 3, roadPointInfo);
        Assert.assertEquals(roadPointInfo, roadArray.get(3, 3));
    }
}
