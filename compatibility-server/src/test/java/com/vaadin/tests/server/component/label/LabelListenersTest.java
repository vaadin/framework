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
package com.vaadin.tests.server.component.label;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Label.ValueChangeEvent;

public class LabelListenersTest extends AbstractListenerMethodsTestBase {

    @Test
    public void testValueChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Label.class, ValueChangeEvent.class,
                ValueChangeListener.class);
    }

    @Test
    public void testValueChangeFiredWhenSettingValue() {
        Label underTest = new Label();

        // setup the mock listener
        ValueChangeListener mockListener = createStrictMock(
                ValueChangeListener.class);
        // record
        mockListener.valueChange(anyObject(ValueChangeEvent.class));

        // test
        underTest.addValueChangeListener(mockListener);

        replay(mockListener);
        underTest.setValue("A new value");

        verify(mockListener);

    }

    @Test
    public void testValueChangeFiredWhenSettingPropertyDataSource() {
        // setup
        Label underTest = new Label();

        Property mockProperty = EasyMock.createMock(Property.class);

        ValueChangeListener mockListener = createStrictMock(
                ValueChangeListener.class);
        // record
        mockListener.valueChange(anyObject(ValueChangeEvent.class));

        expect(mockProperty.getType()).andReturn(String.class).atLeastOnce();
        expect(mockProperty.getValue()).andReturn("Any").atLeastOnce();

        // test

        replay(mockListener, mockProperty);
        underTest.addValueChangeListener(mockListener);
        underTest.setPropertyDataSource(mockProperty);

        verify(mockListener);

    }

    @Test
    public void testValueChangeNotFiredWhenNotSettingValue() {
        Label underTest = new Label();
        // setup the mock listener
        ValueChangeListener mockListener = createStrictMock(
                ValueChangeListener.class);
        // record: nothing to record

        // test
        underTest.addValueChangeListener(mockListener);
        replay(mockListener);
        verify(mockListener);
    }

    @Test
    public void testNoValueChangeFiredWhenSettingPropertyDataSourceToNull() {
        Label underTest = new Label();
        // setup the mock Listener
        ValueChangeListener mockListener = createStrictMock(
                ValueChangeListener.class);
        // record: nothing to record

        // test
        underTest.addValueChangeListener(mockListener);
        underTest.setPropertyDataSource(null);

        replay(mockListener);
        verify(mockListener);
    }

}
