/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.v7.tests.server.component.abstractfield;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.Property.ValueChangeNotifier;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.AbstractField;

/**
 * Base class for tests for checking that value change listeners for fields are
 * not called exactly once when they should be, and not at other times.
 *
 * Does not check all cases (e.g. properties that do not implement
 * {@link ValueChangeNotifier}).
 *
 * Subclasses should implement {@link #setValue()} and call
 * <code>super.setValue(LegacyAbstractField)</code>. Also, subclasses should
 * typically override {@link #setValue(AbstractField)} to set the field value
 * via <code>changeVariables()</code>.
 */
public abstract class AbstractFieldValueChangeTestBase<T> {

    private AbstractField<T> field;
    private ValueChangeListener listener;

    protected void setUp(AbstractField<T> field) {
        this.field = field;
        listener = EasyMock.createStrictMock(ValueChangeListener.class);

    }

    protected ValueChangeListener getListener() {
        return listener;
    }

    /**
     * Test that listeners are not called when they have been unregistered.
     */
    @Test
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
     * LegacyField value change notifications closely mirror value changes of
     * the data source behind the field.
     */
    @Test
    public void testNonBuffered() {
        getField().setPropertyDataSource(new ObjectProperty<String>(""));
        getField().setBuffered(false);

        expectValueChangeFromSetValueNotCommit();
    }

    /**
     * Fully buffered use where the data source is neither read nor modified
     * during editing, and is updated at commit().
     *
     * LegacyField value change notifications reflect the buffered value in the
     * field, not the original data source value changes.
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
