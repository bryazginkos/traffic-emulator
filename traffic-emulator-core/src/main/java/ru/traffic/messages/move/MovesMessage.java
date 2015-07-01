package ru.traffic.messages.move;

import ru.traffic.model.Move;

import java.util.Collection;

/**
 * Created by Константин on 01.07.2015.
 */
public class MovesMessage {
    private final Collection<Move>  moves;

    public MovesMessage(Collection<Move> moves) {
        this.moves = moves;
    }

    public Collection<Move> getMoves() {
        return moves;
    }
}
