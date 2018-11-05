package com.vaadin.tests.components.datefield;

import org.junit.Test;

import com.google.gwt.editor.client.Editor.Ignore;

/**
 * Reuse tests from super DateTimeFieldTestTest class.
 *
 * @author Vaadin Ltd
 *
 */
public class InlineDateTimeFieldTestTest extends DateTimeFieldTestTest {

    @Override
    @Test
    @Ignore
    public void testValueAfterOpeningPopupInRequiredField()
            throws InterruptedException {
        // no popup for inline date field
    }
}
