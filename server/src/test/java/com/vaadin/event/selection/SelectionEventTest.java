package com.vaadin.event.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;

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
        assertEquals("foo", selected.get());

        Mockito.when(event.getValue()).thenReturn(Collections.emptySet());
        assertFalse(event.getFirstSelectedItem().isPresent());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void getFirstSelected_singleSelectEvent() {
        SingleSelectionEvent event = Mockito.mock(SingleSelectionEvent.class);
        Mockito.doCallRealMethod().when(event).getFirstSelectedItem();

        Mockito.when(event.getSelectedItem()).thenReturn(Optional.of("foo"));

        Optional<?> selected = event.getSelectedItem();

        Mockito.verify(event).getSelectedItem();
        assertEquals("foo", selected.get());

        Mockito.when(event.getSelectedItem()).thenReturn(Optional.empty());
        assertFalse(event.getFirstSelectedItem().isPresent());
    }

}
