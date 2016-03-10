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
public abstract class AbstractFieldValueChangeTestBase<T> extends TestCase {

    private AbstractField<T> field;
    private ValueChangeListener listener;

    protected void setUp(AbstractField<T> field) throws Exception {
        this.field = field;
        listener = EasyMock.createStrictMock(ValueChangeListener.class);

    }

    protected ValueChangeListener getListener() {
        return listener;
    }

    /**
     * Test that listeners are not called when they have been unregistered.
     */
    public void testRemoveListener() {
        getField().setPropertyDataSource(new ObjectProperty<String>(""));
        getField().setBuffered(false);

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
    public void testNonBuffered() {
        getField().setPropertyDataSource(new ObjectProperty<String>(""));
        getField().setBuffered(false);

        expectValueChangeFromSetValueNotCommit();
    }

    /**
     * Fully buffered use where the data source is neither read nor modified
     * during editing, and is updated at commit().
     * 
     * Field value change notifications reflect the buffered value in the field,
     * not the original data source value changes.
     */
    public void testBuffered() {
        getField().setPropertyDataSource(new ObjectProperty<String>(""));
        getField().setBuffered(true);

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

    protected AbstractField<T> getField() {
        return field;
    }

    /**
     * Override in subclasses to set value with changeVariables().
     */
    protected void setValue(AbstractField<T> field) {
        field.setValue((T) "newValue");
    }

}
