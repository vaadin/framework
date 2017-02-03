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

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PasswordFieldConstructorTest {

    @Test
    public void initiallyEmpty() {
        PasswordField passwordField = new PasswordField();
        Assert.assertTrue(passwordField.isEmpty());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        PasswordField passwordField = new PasswordField(null, "foobar");
        Assert.assertFalse(passwordField.isEmpty());

        passwordField.clear();
        Assert.assertTrue(passwordField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        PasswordField passwordField = new PasswordField(valueChangeListener);

        passwordField.setValue("value change");

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        PasswordField passwordField = new PasswordField("Caption",
                "Initial value", valueChangeListener);

        verify(valueChangeListener, never())
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

        passwordField.setValue("value change");

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }
}
