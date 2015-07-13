package ru.traffic.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.traffic.model.Move;
import ru.traffic.model.Position;

/**
 * Created by Константин on 13.07.2015.
 */
public class SaveStackTest {

    private RestorableStack<Move> stack;

    @Before
    public void prepare() {
        stack = new RestorableStack<>();
    }

    @Test
    public void testRestore() {

        Position position11 = new Position(1, 1);
        Position position12 = new Position(1, 2);

        Position position21 = new Position(2, 1);
        Position position22 = new Position(2, 2);

        Position position31 = new Position(3, 1);
        Position position32 = new Position(3, 2);

        Position position41 = new Position(4, 1);
        Position position42 = new Position(4, 2);

        Position position51 = new Position(5, 1);
        Position position52 = new Position(5, 2);

        Move move1 = new Move(position11, position12);
        Move move2 = new Move(position21, position22);
        Move move3 = new Move(position31, position32);
        Move move4 = new Move(position41, position42);
        Move move5 = new Move(position51, position52);

        stack.push(move1);
        stack.push(move2);
        stack.push(move3);
        stack.push(move4);
        stack.push(move5);

        stack.save();

        Assert.assertEquals(move5, stack.pop());
        Assert.assertEquals(move4, stack.pop());
        Assert.assertEquals(move3, stack.pop());
        Assert.assertEquals(move2, stack.pop());

        stack.restore();

        Assert.assertEquals(move5, stack.pop());
        Assert.assertEquals(move4, stack.pop());
        Assert.assertEquals(move3, stack.pop());
        Assert.assertEquals(move2, stack.pop());
        Assert.assertEquals(move1, stack.pop());
    }
}
