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
package com.vaadin.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.ClientConnector;

public class AbstractFieldTest extends EasyMockSupport {

    private final class IdentityTextField extends TextField {
        @Override
        protected boolean isDifferentValue(String newValue) {
            // Checks for identity instead of equality
            return newValue != getValue();
        }
    }

    class TextField extends AbstractField<String> {

        String value = "";

        @Override
        public String getValue() {
            return value;
        }

        @Override
        protected void doSetValue(String value) {
            this.value = value;
        }
    }

    TextField field;

    ValueChangeListener<String> l;
    Capture<ValueChangeEvent<String>> capture;

    @Before
    public void setUp() {
        field = new TextField();
        l = mockListener();
        capture = new Capture<>();
    }

    @Test
    public void readOnlyFieldAcceptsValueChangeFromServer() {
        field.setReadOnly(true);
        field.setValue("foo");
        assertEquals("foo", field.getValue());
    }

    @Test
    public void readOnlyFieldIgnoresValueChangeFromClient() {
        field.setReadOnly(true);
        field.setValue("bar", true);
        assertEquals("", field.getValue());
    }

    @Test
    public void valueChangeListenerInvoked() {
        l.valueChange(EasyMock.capture(capture));
        replayAll();

        field.setValue("foo");
        field.addValueChangeListener(l);
        field.setValue("bar");

        assertEventEquals(capture.getValue(), "bar", field, false);

        verifyAll();
    }

    @Test
    public void valueChangeListenerInvokedFromClient() {
        l.valueChange(EasyMock.capture(capture));
        replayAll();

        field.setValue("foo");
        field.addValueChangeListener(l);
        field.setValue("bar", true);

        assertEventEquals(capture.getValue(), "bar", field, true);

        verifyAll();
    }

    @Test
    public void valueChangeListenerNotInvokedIfValueUnchanged() {
        // expect zero invocations of l
        replayAll();

        field.setValue("foo");
        field.addValueChangeListener(l);
        field.setValue("foo");

        verifyAll();
    }

    @Test
    public void valueChangeListenerNotInvokedAfterRemove() {
        // expect zero invocations of l
        replayAll();

        field.addValueChangeListener(l).remove();
        field.setValue("foo");

        verifyAll();
    }

    @SuppressWarnings("unchecked")
    private ValueChangeListener<String> mockListener() {
        return createStrictMock(ValueChangeListener.class);
    }

    private void assertEventEquals(ValueChangeEvent<String> e, String value,
            ClientConnector source, boolean userOriginated) {
        assertEquals("event value", value, e.getValue());
        assertSame("event source", source, e.getSource());
        assertSame("event source connector", source, e.getSource());
        assertEquals("event from user", userOriginated, e.isUserOriginated());
    }

    @Test
    public void identityField_realChange() {
        TextField identityField = new IdentityTextField();

        identityField.addValueChangeListener(l);

        // Expect event to both listeners for actual change
        l.valueChange(EasyMock.capture(capture));

        replayAll();

        identityField.setValue("value");

        verifyAll();
    }

    @Test
    public void identityField_onlyIdentityChange() {
        TextField identityField = new IdentityTextField();
        identityField.setValue("value");

        identityField.addValueChangeListener(l);

        // Expect event to both listeners for actual change
        l.valueChange(EasyMock.capture(capture));

        replayAll();

        String sameValueDifferentIdentity = new String("value");
        identityField.setValue(sameValueDifferentIdentity);

        verifyAll();
    }

    @Test
    public void identityField_noChange() {
        TextField identityField = new IdentityTextField();
        identityField.setValue("value");

        identityField.addValueChangeListener(l);

        // Expect no event for identical value
        replayAll();

        identityField.setValue(identityField.getValue());

        verifyAll();
    }
}
