package ru.traffic.messages.talk;

/**
 * Created by Константин on 11.07.2015.
 */
public class AnsSkipMessage {
    private final boolean willSkip;

    public AnsSkipMessage(boolean willSkip) {
        this.willSkip = willSkip;
    }

    public boolean isWillSkip() {
        return willSkip;
    }
}
