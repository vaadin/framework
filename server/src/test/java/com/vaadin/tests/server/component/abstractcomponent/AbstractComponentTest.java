package com.vaadin.tests.server.component.abstractcomponent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.ui.AbstractComponent;

public class AbstractComponentTest {
    AbstractComponent component = new AbstractComponent() {
    };

    @Test
    public void testImmediate() {
        assertTrue("Component should be immediate by default",
                component.isImmediate());
        component.setImmediate(false);
        assertFalse(
                "Explicitly non-immediate component should not be immediate",
                component.isImmediate());
    }
}
