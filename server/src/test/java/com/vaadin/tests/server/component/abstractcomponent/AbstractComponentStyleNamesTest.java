package com.vaadin.tests.server.component.abstractcomponent;

import junit.framework.TestCase;

import com.vaadin.ui.AbstractComponent;

public class AbstractComponentStyleNamesTest extends TestCase {

    public void testSetMultiple() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2");
        assertEquals(component.getStyleName(), "style1 style2");
    }

    public void testSetAdd() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1");
        component.addStyleName("style2");
        assertEquals(component.getStyleName(), "style1 style2");
    }

    public void testAddSame() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2");
        component.addStyleName("style1");
        assertEquals(component.getStyleName(), "style1 style2");
    }

    public void testSetRemove() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2");
        component.removeStyleName("style1");
        assertEquals(component.getStyleName(), "style2");
    }

    public void testAddRemove() {
        AbstractComponent component = getComponent();
        component.addStyleName("style1");
        component.addStyleName("style2");
        component.removeStyleName("style1");
        assertEquals(component.getStyleName(), "style2");
    }

    public void testRemoveMultipleWithExtraSpaces() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2 style3");
        component.removeStyleName(" style1  style3 ");
        assertEquals(component.getStyleName(), "style2");
    }

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
