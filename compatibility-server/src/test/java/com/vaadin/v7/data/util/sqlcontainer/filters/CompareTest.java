package com.vaadin.v7.data.util.sqlcontainer.filters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.util.filter.Compare;

public class CompareTest {

    @Test
    public void testEquals() {
        Compare c1 = new Compare.Equal("prop1", "val1");
        Compare c2 = new Compare.Equal("prop1", "val1");
        assertTrue(c1.equals(c2));
    }

    @Test
    public void testDifferentTypeEquals() {
        Compare c1 = new Compare.Equal("prop1", "val1");
        Compare c2 = new Compare.Greater("prop1", "val1");
        assertFalse(c1.equals(c2));
    }

    @Test
    public void testEqualsNull() {
        Compare c1 = new Compare.Equal("prop1", "val1");
        assertFalse(c1.equals(null));
    }
}
