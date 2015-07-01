package ru.traffic.view.util;

import org.springframework.stereotype.Component;
import ru.traffic.util.RoadArray;
import ru.traffic.view.json.Road;

/**
 * Created by Константин on 01.07.2015.
 */
@Component
public class RoadTransformer {

    public Road convert(RoadArray roadArray) {
        Road road = new Road();
        int lanesNumber = roadArray.getLanesNumber();
        int length = roadArray.getLength();

        road.setLanes(lanesNumber);
        road.setLength(length);

        int[][] viewArray = new int[length][lanesNumber];
        for (int i = 1; i <= length; i++) {
            for (int j = 1; j <= lanesNumber; j++) {
                viewArray[i - 1][j - 1] = roadArray.get(i, j) != null ? 1 : 0;
            }
        }
        road.setPoints(viewArray);
        return road;
    }
}
