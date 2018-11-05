package com.vaadin.v7.tests.server.component.abstractfield;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;

/**
 * Tests declarative support for implementations of {@link AbstractField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class AbstractFieldDeclarativeTest
        extends DeclarativeTestBase<AbstractField<?>> {

    @Test
    public void testPlainText() {
        // FIXME
        // String design = "<vaadin-text-field readonly tabindex=3"
        // + "required"
        // + "/>";
        // AbstractField<String> tf = new TextField();
        // tf.setRequired(true);
        // tf.setTabIndex(3);
        // tf.setReadOnly(true);
        // testRead(design, tf);
        // testWrite(design, tf);
        //
        // // Test with readonly=false
        // design = design.replace("readonly", "");
        // tf.setReadOnly(false);
        // testRead(design, tf);
        // testWrite(design, tf);
    }

    @Test
    public void testModelReadOnly() {
        // Test that read only value coming from property data source is not
        // written to design.
        String design = "<vaadin-text-field readonly value=test></vaadin-text-field>";
        AbstractField<String> component = new TextField();
        component.setReadOnly(true);
        component.setValue("test");
        // FIXME (?) current implementation only
        // disables client-side modification
        testWrite(design, component);
    }
}
