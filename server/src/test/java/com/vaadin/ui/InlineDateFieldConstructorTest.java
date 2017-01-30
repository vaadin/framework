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

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.datefield.DateResolution;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class InlineDateFieldConstructorTest {

    @Test
    public void initiallyEmpty() {
        InlineDateField dateField = new InlineDateField();
        Assert.assertTrue(dateField.isEmpty());
        Assert.assertEquals(DateResolution.DAY, dateField.getResolution());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        InlineDateField dateField = new InlineDateField(null, LocalDate.now());
        Assert.assertEquals(DateResolution.DAY, dateField.getResolution());
        Assert.assertFalse(dateField.isEmpty());

        dateField.clear();
        Assert.assertTrue(dateField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        InlineDateField dateField = new InlineDateField(valueChangeListener);
        Assert.assertEquals(DateResolution.DAY, dateField.getResolution());

        dateField.setValue(LocalDate.now());

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        InlineDateField dateField = new InlineDateField("Caption",
                LocalDate.now(), valueChangeListener);
        Assert.assertEquals(DateResolution.DAY, dateField.getResolution());

        verify(valueChangeListener, never())
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

        dateField.setValue(LocalDate.now().plusDays(1));

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }
}
