package ru.traffic.model;

import com.google.common.base.Objects;

/**
 * Created by Константин on 30.06.2015.
 */
public class Move {
    private Position from;
    private Position to;

    public Move(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    public Position getFrom() {
        return from;
    }

    public void setFrom(Position from) {
        this.from = from;
    }

    public Position getTo() {
        return to;
    }

    public void setTo(Position to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Move{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return Objects.equal(from, move.from) &&
                Objects.equal(to, move.to);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(from, to);
    }
}
