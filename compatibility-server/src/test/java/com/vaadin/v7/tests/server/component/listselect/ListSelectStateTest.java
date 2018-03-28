package com.vaadin.v7.tests.server.component.listselect;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.v7.shared.ui.select.AbstractSelectState;
import com.vaadin.v7.ui.ListSelect;

/**
 * Tests for ListSelect State.
 *
 */
public class ListSelectStateTest {

    @Test
    public void getState_listSelectHasCustomState() {
        TestListSelect select = new TestListSelect();
        AbstractSelectState state = select.getState();
        assertEquals("Unexpected state class", AbstractSelectState.class,
                state.getClass());
    }

    private static class TestListSelect extends ListSelect {
        @Override
        public AbstractSelectState getState() {
            return super.getState();
        }
    }

}
