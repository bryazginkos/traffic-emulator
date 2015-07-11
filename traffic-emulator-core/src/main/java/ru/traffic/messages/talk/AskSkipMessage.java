package ru.traffic.messages.talk;

import ru.traffic.model.Move;

/**
 * Created by Константин on 11.07.2015.
 */
public class AskSkipMessage {

    private final Move wishMove;

    public AskSkipMessage(Move wishMove) {
        this.wishMove = wishMove;
    }

    public Move getWishMove() {
        return wishMove;
    }
}
