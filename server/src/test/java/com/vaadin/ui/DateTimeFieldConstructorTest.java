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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.datefield.DateTimeResolution;

public class DateTimeFieldConstructorTest {

    @Test
    public void initiallyEmpty() {
        DateTimeField dateTimeField = new DateTimeField();
        assertTrue(dateTimeField.isEmpty());
        assertEquals(DateTimeResolution.MINUTE, dateTimeField.getResolution());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        DateTimeField dateTimeField = new DateTimeField(null,
                LocalDateTime.now());
        assertFalse(dateTimeField.isEmpty());
        assertEquals(DateTimeResolution.MINUTE, dateTimeField.getResolution());

        dateTimeField.clear();
        assertTrue(dateTimeField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        DateTimeField dateTimeField = new DateTimeField(valueChangeListener);
        assertEquals(DateTimeResolution.MINUTE, dateTimeField.getResolution());

        dateTimeField.setValue(LocalDateTime.now());

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        DateTimeField dateTimeField = new DateTimeField("Caption",
                LocalDateTime.now(), valueChangeListener);
        assertEquals(DateTimeResolution.MINUTE, dateTimeField.getResolution());

        verify(valueChangeListener, never())
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

        dateTimeField.setValue(LocalDateTime.now().plusDays(1));

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }
}
