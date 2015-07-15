package ru.traffic.util;

import java.util.Stack;

/**
 * Created by Константин on 13.07.2015.
 */
public class RestorableStack<T> extends Stack<T> {

    private Stack<T> saved;

    private T first;

    public void save() {
        saved = new Stack<>();
        saved.addAll(this);
    }

    public void restore() {
        clear();
        addAll(saved);
    }

    @Override
    public synchronized T pop() {
        if (isEmpty()) {
            return first;
        }
        return super.pop();
    }

    @Override
    public T push(T item) {
        if (isEmpty()) {
            first = item;
        }
        return super.push(item);
    }
}
