package com.vaadin.tests.server.component.textfield;

import java.util.Collections;

import junit.framework.TestCase;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.TextField;

public class TextFieldWithPropertyFormatterTest extends TestCase {

    private static final String INPUT_VALUE = "foo";
    private static final String PARSED_VALUE = "BAR";
    private static final String FORMATTED_VALUE = "FOOBAR";
    private static final String ORIGINAL_VALUE = "Original";
    private TextField field;
    private PropertyFormatter<String> formatter;
    private ObjectProperty<String> property;
    private ValueChangeListener listener;
    private int listenerCalled;
    private int repainted;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        field = new TextField() {
            @Override
            public void markAsDirty() {
                repainted++;
                super.markAsDirty();
            }
        };

        formatter = new PropertyFormatter<String>() {

            @Override
            public String parse(String formattedValue) throws Exception {
                assertEquals(INPUT_VALUE, formattedValue);
                return PARSED_VALUE;
            }

            @Override
            public String format(String value) {
                return FORMATTED_VALUE;
            }
        };

        property = new ObjectProperty<String>(ORIGINAL_VALUE);

        formatter.setPropertyDataSource(property);
        field.setPropertyDataSource(formatter);

        listener = new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                listenerCalled++;
                assertEquals(1, listenerCalled);
                assertEquals(FORMATTED_VALUE, event.getProperty().getValue());
            }
        };

        field.addListener(listener);
        listenerCalled = 0;
        repainted = 0;
    }

    public void testWithServerApi() {
        checkInitialState();

        field.setValue(INPUT_VALUE);

        checkEndState();

    }

    private void checkEndState() {
        assertEquals(1, listenerCalled);
        assertTrue(repainted >= 1);
        assertEquals(FORMATTED_VALUE, field.getValue());
        assertEquals(FORMATTED_VALUE, formatter.getValue());
        assertEquals(PARSED_VALUE, property.getValue());
    }

    private void checkInitialState() {
        assertEquals(ORIGINAL_VALUE, property.getValue());
        assertEquals(FORMATTED_VALUE, formatter.getValue());
        assertEquals(FORMATTED_VALUE, field.getValue());
    }

    public void testWithSimulatedClientSideChange() {
        checkInitialState();

        field.changeVariables(null,
                Collections.singletonMap("text", (Object) INPUT_VALUE));

        checkEndState();

    }

}
