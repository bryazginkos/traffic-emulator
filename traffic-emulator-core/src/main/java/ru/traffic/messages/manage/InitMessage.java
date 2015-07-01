package ru.traffic.messages.manage;

/**
 * Created by Константин on 01.07.2015.
 */
public class InitMessage {
    private final int lanes;
    private final int length;

    public InitMessage(int lanes, int length) {
        this.lanes = lanes;
        this.length = length;
    }

    public int getLanes() {
        return lanes;
    }

    public int getLength() {
        return length;
    }
}
