package com.vaadin.tests.server.components;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.EasyMock;

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
public class TestTextFieldValueChange extends AbstractTestFieldValueChange {

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
    protected void setValue(AbstractField field) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("text", "newValue");
        field.changeVariables(field, variables);
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
        getField().setWriteThrough(true);
        getField().setReadThrough(true);

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
     * If read through is on and value has been modified, but not committed, the
     * value should not propagate similar to
     * {@link #testValueChangeEventPropagationWithReadThrough()}
     * 
     * TODO make test field type agnostic (eg. combobox)
     */
    public void testValueChangePropagationWithReadThroughWithModifiedValue() {
        final String initialValue = "initial";
        ObjectProperty<String> property = new ObjectProperty<String>(
                initialValue);
        getField().setPropertyDataSource(property);

        // write buffering on, read buffering off
        getField().setWriteThrough(false);
        getField().setReadThrough(true);

        // Expect no value changes calls to listener
        EasyMock.replay(getListener());

        // first set the value (note, write through false -> not forwarded to
        // property)
        setValue(getField());

        Assert.assertTrue(getField().isModified());

        // Add listener and set the value -> should end up in listener once
        getField().addListener(getListener());

        // modify property value, should not fire value change in field as the
        // field has uncommitted value (aka isModified() == true)
        property.setValue("Foo");

        // Ensure listener was called once
        EasyMock.verify(getListener());

        // get value should not fire value change again
        Object value = getField().getValue();
        // Ensure listener still has been called only once
        EasyMock.verify(getListener());

        // field value should be different from the original value and current
        // proeprty value
        boolean isValueEqualToInitial = value.equals(initialValue);
        Assert.assertFalse(isValueEqualToInitial);
        boolean isValueEqualToPropertyValue = value.equals(property.getValue());
        Assert.assertFalse(isValueEqualToPropertyValue);

        // Ensure listener has not been called
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
        getField().setWriteThrough(false);
        getField().setReadThrough(false);

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
