package com.vaadin.tests.components.abstractfield;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;

public class AbstractComponentStyleNameTest {

    AbstractComponent c;

    @Before
    public void setup() {
        c = new Button();
    }

    @Test
    public void add() {
        c.addStyleName("foo");
        assertEquals("foo", c.getStyleName());
    }

    @Test
    public void addWithHeadingSpace() {
        c.addStyleName(" foo");
        assertEquals("foo", c.getStyleName());
    }

    @Test
    public void addWithTrailingSpace() {
        c.addStyleName("foo ");
        assertEquals("foo", c.getStyleName());
    }

    @Test
    public void removeWithHeadingSpace() {
        c.setStyleName("foo");
        c.removeStyleName(" foo");
        assertEquals("", c.getStyleName());
    }

    @Test
    public void removeWithTrailingSpace() {
        c.setStyleName("foo");
        c.removeStyleName("foo ");
        assertEquals("", c.getStyleName());
    }

    @Test
    public void addMultipleTimes() {
        c.addStyleName("foo");
        c.addStyleName("foo");
        assertEquals("foo", c.getStyleName());
    }

    @Test
    public void setStyleAdd() {
        c.setStyleName("foo", true);
        assertEquals("foo", c.getStyleName());
    }

    @Test
    public void setStyleMultipleAdd() {
        c.setStyleName("foo", true);
        c.setStyleName("foo", true);
        assertEquals("foo", c.getStyleName());
    }

    @Test
    public void setStyleRemove() {
        c.addStyleName("foo");
        c.setStyleName("foo", false);
        assertEquals("", c.getStyleName());
    }

    @Test
    public void setStyleMultipleRemove() {
        c.addStyleName("foo");
        c.setStyleName("foo", false);
        c.setStyleName("foo", false);
        assertEquals("", c.getStyleName());
    }

    @Test
    public void remove() {
        c.addStyleName("foo");
        c.removeStyleName("foo");
        assertEquals("", c.getStyleName());
    }

    @Test
    public void removeMultipleTimes() {
        c.addStyleName("foo");
        c.removeStyleName("foo");
        c.removeStyleName("foo");
        assertEquals("", c.getStyleName());
    }
}
