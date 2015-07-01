package ru.traffic.messages.move;

import ru.traffic.model.Move;
import ru.traffic.model.Position;

/**
 * Created by Константин on 30.06.2015.
 */
public class MoveMessage {

    private final Move move;

    public MoveMessage(Move move) {
        this.move = move;
    }

    public Move getMove() {
        return move;
    }
}
