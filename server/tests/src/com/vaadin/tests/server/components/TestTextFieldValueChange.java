package com.vaadin.tests.server.components;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;

/**
 * Check that the value change listener for a text field is triggered exactly
 * once when setting the value, at the correct time.
 * 
 * See <a href="http://dev.vaadin.com/ticket/4394">Ticket 4394</a>.
 */
public class TestTextFieldValueChange extends
        AbstractTestFieldValueChange<String> {

    @Override
    protected void setUp() throws Exception {
        super.setUp(new TextField());
    }

    /**
     * Case where the text field only uses its internal buffer, no external
     * property data source.
     */
    public void testNoDataSource() {
        getField().setPropertyDataSource(null);

        expectValueChangeFromSetValueNotCommit();
    }

    @Override
    protected void setValue(AbstractField<String> field) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("text", "newValue");
        ((TextField) field).changeVariables(field, variables);
    }

    /**
     * Test that field propagates value change events originating from property,
     * but don't fire value change events twice if value has only changed once.
     * 
     * 
     * TODO make test field type agnostic (eg. combobox)
     */
    public void testValueChangeEventPropagationWithReadThrough() {
        ObjectProperty<String> property = new ObjectProperty<String>("");
        getField().setPropertyDataSource(property);

        // defaults, buffering off
        getField().setBuffered(false);

        // Expectations and start test
        getListener().valueChange(EasyMock.isA(ValueChangeEvent.class));
        EasyMock.replay(getListener());

        // Add listener and set the value -> should end up in listener once
        getField().addListener(getListener());

        property.setValue("Foo");

        // Ensure listener was called once
        EasyMock.verify(getListener());

        // get value should not fire value change again
        Object value = getField().getValue();
        Assert.assertEquals("Foo", value);

        // Ensure listener still has been called only once
        EasyMock.verify(getListener());
    }

    /**
     * Value change events from property should not propagate if read through is
     * false. Execpt when the property is being set.
     * 
     * TODO make test field type agnostic (eg. combobox)
     */
    public void testValueChangePropagationWithReadThroughOff() {
        final String initialValue = "initial";
        ObjectProperty<String> property = new ObjectProperty<String>(
                initialValue);

        // set buffering
        getField().setBuffered(true);

        // Value change should only happen once, when setting the property,
        // further changes via property should not cause value change listener
        // in field to be notified
        getListener().valueChange(EasyMock.isA(ValueChangeEvent.class));
        EasyMock.replay(getListener());

        getField().addListener(getListener());
        getField().setPropertyDataSource(property);

        // Ensure listener was called once
        EasyMock.verify(getListener());

        // modify property value, should not fire value change in field as the
        // read buffering is on (read through == false)
        property.setValue("Foo");

        // Ensure listener still has been called only once
        EasyMock.verify(getListener());

        // get value should not fire value change again
        Object value = getField().getValue();

        // field value should be different from the original value and current
        // proeprty value
        boolean isValueEqualToInitial = value.equals(initialValue);
        Assert.assertTrue(isValueEqualToInitial);

        // Ensure listener still has been called only once
        EasyMock.verify(getListener());

    }

}
