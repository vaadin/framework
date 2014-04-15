package com.vaadin.tests.util;

import java.util.AbstractCollection;
import java.util.Iterator;

public class RangeCollection extends AbstractCollection<Integer> {

    public static class RangeIterator implements Iterator<Integer> {

        private int value;
        private int max;

        public RangeIterator(int max) {
            this.max = max;
            value = 0;
        }

        @Override
        public boolean hasNext() {
            return (value < max - 1);
        }

        @Override
        public Integer next() {
            return value++;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private int size = 0;

    public RangeCollection(int size) {
        this.size = size;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new RangeIterator(size - 1);
    }

    @Override
    public int size() {
        return size;
    }

}
