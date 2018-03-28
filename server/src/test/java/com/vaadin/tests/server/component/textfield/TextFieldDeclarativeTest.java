package com.vaadin.tests.server.component.textfield;

import org.junit.Test;

import com.vaadin.tests.server.component.abstracttextfield.AbstractTextFieldDeclarativeTest;
import com.vaadin.ui.TextField;

/**
 * Tests declarative support for implementations of {@link TextField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TextFieldDeclarativeTest
        extends AbstractTextFieldDeclarativeTest<TextField> {

    @Override
    @Test
    public void valueDeserialization()
            throws InstantiationException, IllegalAccessException {
        String value = "foo";
        String design = String.format("<%s value='%s'/>", getComponentTag(),
                value);

        TextField component = getComponentClass().newInstance();
        component.setValue(value);
        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    @Test
    public void readOnlyValue()
            throws InstantiationException, IllegalAccessException {
        String value = "foo";
        String design = String.format("<%s readonly value='%s'/>",
                getComponentTag(), value);

        TextField component = getComponentClass().newInstance();
        component.setValue(value);

        component.setReadOnly(true);
        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-text-field";
    }

    @Override
    protected Class<? extends TextField> getComponentClass() {
        return TextField.class;
    }

}
