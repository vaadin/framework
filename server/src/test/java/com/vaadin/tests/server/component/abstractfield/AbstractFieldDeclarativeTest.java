package com.vaadin.tests.server.component.abstractfield;

import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;
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
        String design = "<vaadin-text-field buffered validation-visible='false' invalid-committed"
                + " invalid-allowed='false' required required-error='This is a required field'"
                + " conversion-error='Input {0} cannot be parsed' tabindex=3 readonly/>";
        AbstractField tf = new TextField();
        tf.setBuffered(true);
        tf.setBuffered(true);
        tf.setValidationVisible(false);
        tf.setInvalidCommitted(true);
        tf.setInvalidAllowed(false);
        tf.setRequired(true);
        tf.setRequiredError("This is a required field");
        tf.setConversionError("Input {0} cannot be parsed");
        tf.setTabIndex(3);
        tf.setReadOnly(true);
        testRead(design, tf);
        testWrite(design, tf);

        // Test with readonly=false
        design = design.replace("readonly", "");
        tf.setReadOnly(false);
        testRead(design, tf);
        testWrite(design, tf);
    }

    @Test
    public void testModelReadOnly() {
        // Test that read only value coming from property data source is not
        // written to design.
        String design = "<vaadin-text-field value=test></vaadin-text-field>";
        AbstractField component = new TextField();
        ObjectProperty<String> property = new ObjectProperty<String>("test");
        property.setReadOnly(true);
        component.setPropertyDataSource(property);
        testWrite(design, component);
    }
}
