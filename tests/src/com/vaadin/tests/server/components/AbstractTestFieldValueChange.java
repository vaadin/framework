package com.vaadin.tests.server.components;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractField;

/**
 * Base class for tests for checking that value change listeners for fields are
 * not called exactly once when they should be, and not at other times.
 * 
 * Does not check all cases (e.g. properties that do not implement
 * {@link ValueChangeNotifier}).
 * 
 * Subclasses should implement {@link #setValue()} and call
 * <code>super.setValue(AbstractField)</code>. Also, subclasses should typically
 * override {@link #setValue(AbstractField)} to set the field value via
 * <code>changeVariables()</code>.
 */
public abstract class AbstractTestFieldValueChange extends TestCase {

    private AbstractField field;
    private ValueChangeListener listener;

    protected void setUp(AbstractField field) throws Exception {
        this.field = field;
        listener = EasyMock.createStrictMock(ValueChangeListener.class);

    }

    /**
     * Test that listeners are not called when they have been unregistered.
     */
    public void testRemoveListener() {
        getField().setPropertyDataSource(new ObjectProperty(""));
        getField().setWriteThrough(true);
        getField().setReadThrough(true);

        // Expectations and start test
        listener.valueChange(EasyMock.isA(ValueChangeEvent.class));
        EasyMock.replay(listener);

        // Add listener and set the value -> should end up in listener once
        getField().addListener(listener);
        setValue(getField());

        // Ensure listener was called once
        EasyMock.verify(listener);

        // Remove the listener and set the value -> should not end up in
        // listener
        getField().removeListener(listener);
        setValue(getField());

        // Ensure listener still has been called only once
        EasyMock.verify(listener);
    }

    /**
     * Common unbuffered case: both writeThrough (auto-commit) and readThrough
     * are on. Calling commit() should not cause notifications.
     * 
     * Using the readThrough mode allows changes made to the property value to
     * be seen in some cases also when there is no notification of value change
     * from the property.
     * 
     * Field value change notifications closely mirror value changes of the data
     * source behind the field.
     */
    public void testWriteThroughReadThrough() {
        getField().setPropertyDataSource(new ObjectProperty(""));
        getField().setWriteThrough(true);
        getField().setReadThrough(true);

        expectValueChangeFromSetValueNotCommit();
    }

    /**
     * Fully buffered use where the data source is neither read nor modified
     * during editing, and is updated at commit().
     * 
     * Field value change notifications reflect the buffered value in the field,
     * not the original data source value changes.
     */
    public void testNoWriteThroughNoReadThrough() {
        getField().setPropertyDataSource(new ObjectProperty(""));
        getField().setWriteThrough(false);
        getField().setReadThrough(false);

        expectValueChangeFromSetValueNotCommit();
    }

    /**
     * Less common partly buffered case: writeThrough (auto-commit) is on and
     * readThrough is off. Calling commit() should not cause notifications.
     * 
     * Without readThrough activated, changes to the data source that do not
     * cause notifications are not reflected by the field value.
     * 
     * Field value change notifications correspond to changes made to the data
     * source value through the text field or the (notifying) property.
     */
    public void testWriteThroughNoReadThrough() {
        getField().setPropertyDataSource(new ObjectProperty(""));
        getField().setWriteThrough(true);
        getField().setReadThrough(false);

        expectValueChangeFromSetValueNotCommit();
    }

    /**
     * Partly buffered use where the data source is read but not nor modified
     * during editing, and is updated at commit().
     * 
     * When used like this, a field is updated from the data source if necessary
     * when its value is requested and the property value has changed but the
     * field has not been modified in its buffer.
     * 
     * Field value change notifications reflect the buffered value in the field,
     * not the original data source value changes.
     */
    public void testNoWriteThroughReadThrough() {
        getField().setPropertyDataSource(new ObjectProperty(""));
        getField().setWriteThrough(false);
        getField().setReadThrough(true);

        expectValueChangeFromSetValueNotCommit();
    }

    protected void expectValueChangeFromSetValueNotCommit() {
        // Expectations and start test
        listener.valueChange(EasyMock.isA(ValueChangeEvent.class));
        EasyMock.replay(listener);

        // Add listener and set the value -> should end up in listener once
        getField().addListener(listener);
        setValue(getField());

        // Ensure listener was called once
        EasyMock.verify(listener);

        // commit
        getField().commit();

        // Ensure listener was not called again
        EasyMock.verify(listener);
    }

    protected AbstractField getField() {
        return field;
    }

    /**
     * Override in subclasses to set value with changeVariables().
     */
    protected void setValue(AbstractField field) {
        field.setValue("newValue");
    }

}
