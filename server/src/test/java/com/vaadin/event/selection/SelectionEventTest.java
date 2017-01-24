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
package com.vaadin.event.selection;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Vaadin Ltd
 *
 */
public class SelectionEventTest {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void getFirstSelected_mutliSelectEvent() {
        MultiSelectionEvent<?> event = Mockito.mock(MultiSelectionEvent.class);
        Mockito.doCallRealMethod().when(event).getFirstSelectedItem();

        Mockito.when(event.getValue())
                .thenReturn(new LinkedHashSet(Arrays.asList("foo", "bar")));

        Optional<?> selected = event.getFirstSelectedItem();

        Mockito.verify(event).getValue();
        Assert.assertEquals("foo", selected.get());

        Mockito.when(event.getValue()).thenReturn(Collections.emptySet());
        Assert.assertFalse(event.getFirstSelectedItem().isPresent());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void getFirstSelected_singleSelectEvent() {
        SingleSelectionEvent event = Mockito.mock(SingleSelectionEvent.class);
        Mockito.doCallRealMethod().when(event).getFirstSelectedItem();

        Mockito.when(event.getSelectedItem()).thenReturn(Optional.of("foo"));

        Optional<?> selected = event.getSelectedItem();

        Mockito.verify(event).getSelectedItem();
        Assert.assertEquals("foo", selected.get());

        Mockito.when(event.getSelectedItem()).thenReturn(Optional.empty());
        Assert.assertFalse(event.getFirstSelectedItem().isPresent());
    }

}
