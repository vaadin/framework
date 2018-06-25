package com.vaadin.tests.server.component.textfield;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.TextField;

/**
 * Tests declarative support for implementations of {@link TextField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TextFieldDeclarativeTest extends DeclarativeTestBase<TextField> {

    @Test
    public void testEmpty() {
        String design = "<vaadin-text-field/>";
        TextField tf = new TextField();
        testRead(design, tf);
        testWrite(design, tf);
    }

    @Test
    public void testValue() {
        String design = "<vaadin-text-field value=\"test value\"/>";
        TextField tf = new TextField();
        tf.setValue("test value");
        testRead(design, tf);
        testWrite(design, tf);
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin-text-field readonly value=\"test value\"/>";
        TextField tf = new TextField();
        tf.setValue("test value");
        tf.setReadOnly(true);
        testRead(design, tf);
        testWrite(design, tf);
    }
}
