package ru.traffic.view.util;

import org.springframework.stereotype.Component;
import ru.traffic.car.CarImpl;
import ru.traffic.model.RoadPointInfo;
import ru.traffic.util.RoadArray;
import ru.traffic.view.json.Road;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ���������� on 01.07.2015.
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
        double hue = politeness * 55/240;
        double brightness = (69 + (210 - 69)*(1 -effrontery))/240;
        Color c = Color.getHSBColor((float)hue, 1, (float)brightness);
        //calculated using linear function with move from PE plane to color plane
        long red = Math.round(c.getRed());
        long green = Math.round(c.getGreen());
        long blue = Math.round(c.getBlue());
        return "#" + toHexString(red) + toHexString(green) + toHexString(blue);
    }

    private String toHexString(long arg) {
        String result = Long.toHexString(arg);
        if (result.length() == 1) {
            result = "0" + result;
        }
        return result;
    }

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setSize(840,560);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().add(new Grid());
        window.setVisible(true);
    }

    private static class Grid extends JComponent {
        public void paint(Graphics g) {
            double dp = 0.1;
            double de = 0.1;
            double p = 0;
            double e = 0;
            while (p <= 1) {
                while (e <=1) {
                    double hue = p * 55/240;
                    double brightness = (69 + (210 - 69)*(1 -e))/240;
                    Color c = Color.getHSBColor((float)hue, 1, (float)brightness);
                    g.setColor(c);
                    g.fillRect((int)Math.round(p* 100), (int)Math.round(e*100), 100, 100);
                    e += de;
                }
                p += dp;
                e = 0;
            }
        }
    }
}
