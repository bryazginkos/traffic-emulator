package ru.traffic.util;

import java.util.Stack;

/**
 * Created by Константин on 13.07.2015.
 */
public class RestorableStack<T> extends Stack<T> {

    private Stack<T> saved;

    public void save() {
        saved = new Stack<>();
        saved.addAll(this);
    }

    public void restore() {
        clear();
        addAll(saved);
    }

}
