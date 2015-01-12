package com.vaadin.tests.server.component.label;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMock;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.Label.ValueChangeEvent;

public class LabelListenersTest extends AbstractListenerMethodsTestBase {
    public void testValueChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Label.class, ValueChangeEvent.class,
                ValueChangeListener.class);
    }

    public void testValueChangeFiredWhenSettingValue() {
        Label underTest = new Label();

        // setup the mock listener
        ValueChangeListener mockListener = createStrictMock(ValueChangeListener.class);
        // record
        mockListener
                .valueChange(anyObject(com.vaadin.data.Property.ValueChangeEvent.class));

        // test
        underTest.addValueChangeListener(mockListener);

        replay(mockListener);
        underTest.setValue("A new value");

        verify(mockListener);

    }

    public void testValueChangeFiredWhenSettingPropertyDataSource() {
        // setup
        Label underTest = new Label();

        Property mockProperty = EasyMock.createMock(Property.class);

        ValueChangeListener mockListener = createStrictMock(ValueChangeListener.class);
        // record
        mockListener
                .valueChange(anyObject(com.vaadin.data.Property.ValueChangeEvent.class));

        expect(mockProperty.getType()).andReturn(String.class).atLeastOnce();
        expect(mockProperty.getValue()).andReturn("Any").atLeastOnce();

        // test

        replay(mockListener, mockProperty);
        underTest.addValueChangeListener(mockListener);
        underTest.setPropertyDataSource(mockProperty);

        verify(mockListener);

    }

    public void testValueChangeNotFiredWhenNotSettingValue() {
        Label underTest = new Label();
        // setup the mock listener
        ValueChangeListener mockListener = createStrictMock(ValueChangeListener.class);
        // record: nothing to record

        // test
        underTest.addValueChangeListener(mockListener);
        replay(mockListener);
        verify(mockListener);
    }

    public void testNoValueChangeFiredWhenSettingPropertyDataSourceToNull() {
        Label underTest = new Label();
        // setup the mock Listener
        ValueChangeListener mockListener = createStrictMock(ValueChangeListener.class);
        // record: nothing to record

        // test
        underTest.addValueChangeListener(mockListener);
        underTest.setPropertyDataSource(null);

        replay(mockListener);
        verify(mockListener);
    }

}
