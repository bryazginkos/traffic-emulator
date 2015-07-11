package ru.traffic.view.util;

import org.springframework.stereotype.Component;
import ru.traffic.car.CarImpl;
import ru.traffic.model.RoadPointInfo;
import ru.traffic.util.RoadArray;
import ru.traffic.view.json.Road;

/**
 * Created by Константин on 01.07.2015.
 */
@Component
public class RoadTransformer {

    public Road convert(RoadArray<RoadPointInfo> roadArray) {
        Road road = new Road();
        int lanesNumber = roadArray.getLanesNumber();
        int length = roadArray.getLength();

        road.setLanes(lanesNumber);
        road.setLength(length);

        String[][] viewArray = new String[length][lanesNumber];
        for (int i = 1; i <= length; i++) {
            for (int j = 1; j <= lanesNumber; j++) {
                RoadPointInfo roadPointInfo = roadArray.get(i, j);
                if (roadPointInfo != null) {
                    CarImpl car = (CarImpl) roadPointInfo.getCar(); //other Cars maybe don't have such params
                    if (car.wishSpeed() > 0) {
                        double politeness = car.getPoliteness();
                        double effrontery = car.getEffrontery();
                        viewArray[i - 1][j - 1] = "#" + getColor(politeness, effrontery);
                    } else {
                        viewArray[i - 1][j - 1] = "#000000";
                    }
                }
            }
        }
        road.setPoints(viewArray);
        return road;
    }

    private String getColor(double politeness, double effrontery) {
        //calculated using linear function with move from PE plane to color plane
        long red = Math.round(-politeness * 66 - effrontery * 184 + 253);
        long green = Math.round(politeness * 66 - effrontery * 148 + 187);
        long blue = Math.round(politeness * 5 - effrontery * 81 + 187);
        return toHexString(red) + toHexString(green) + toHexString(blue);
    }

    private String toHexString(long arg) {
        String result = Long.toHexString(arg);
        if (result.length() == 1) {
            result = "0" + result;
        }
        return result;
    }
}
