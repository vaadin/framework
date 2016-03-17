package com.vaadin.tests.server.component.abstractcomponent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.ui.AbstractComponent;

public class AbstractComponentStyleNamesTest {

    @Test
    public void testSetMultiple() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2");
        assertEquals(component.getStyleName(), "style1 style2");
    }

    @Test
    public void testSetAdd() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1");
        component.addStyleName("style2");
        assertEquals(component.getStyleName(), "style1 style2");
    }

    @Test
    public void testAddSame() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2");
        component.addStyleName("style1");
        assertEquals(component.getStyleName(), "style1 style2");
    }

    @Test
    public void testSetRemove() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2");
        component.removeStyleName("style1");
        assertEquals(component.getStyleName(), "style2");
    }

    @Test
    public void testAddRemove() {
        AbstractComponent component = getComponent();
        component.addStyleName("style1");
        component.addStyleName("style2");
        component.removeStyleName("style1");
        assertEquals(component.getStyleName(), "style2");
    }

    @Test
    public void testRemoveMultipleWithExtraSpaces() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2 style3");
        component.removeStyleName(" style1  style3 ");
        assertEquals(component.getStyleName(), "style2");
    }

    @Test
    public void testSetWithExtraSpaces() {
        AbstractComponent component = getComponent();
        component.setStyleName(" style1  style2 ");
        assertEquals(component.getStyleName(), "style1 style2");
    }

    private AbstractComponent getComponent() {
        return new AbstractComponent() {
        };
    }
}
