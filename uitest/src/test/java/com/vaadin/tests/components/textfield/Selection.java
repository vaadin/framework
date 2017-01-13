package com.vaadin.tests.components.textfield;

import java.util.List;

public class Selection {
    private int start, length;

    public Selection(List<Long> range) {
        start = range.get(0).intValue();
        length = range.get(1).intValue() - start;
    }

    public Selection(int start, int length) {
        super();
        this.start = start;
        this.length = length;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        result = prime * result + start;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Selection other = (Selection) obj;
        if (length != other.length) {
            return false;
        }
        if (start != other.start) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Selection [start=" + start + ", length=" + length + "]";
    }

}
