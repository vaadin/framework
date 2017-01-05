package com.vaadin.v7.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AbstractLegacyComponentTest {
    AbstractLegacyComponent component = new AbstractLegacyComponent() {
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
