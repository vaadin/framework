package com.vaadin.v7.tests.server.component.abstractselect;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.v7.shared.ui.select.AbstractSelectState;
import com.vaadin.v7.ui.AbstractSelect;

/**
 * Tests for AbstractSelect state
 *
 */
public class AbstractSelectStateTest {

    @Test
    public void getState_selectHasCustomState() {
        TestSelect select = new TestSelect();
        AbstractSelectState state = select.getState();
        assertEquals("Unexpected state class", AbstractSelectState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_selectHasCustomPrimaryStyleName() {
        TestSelect combobox = new TestSelect();
        AbstractSelectState state = new AbstractSelectState();
        assertEquals("Unexpected primary style name", state.primaryStyleName,
                combobox.getPrimaryStyleName());
    }

    @Test
    public void selectStateHasCustomPrimaryStyleName() {
        AbstractSelectState state = new AbstractSelectState();
        assertEquals("Unexpected primary style name", "v-select",
                state.primaryStyleName);
    }

    private static class TestSelect extends AbstractSelect {

        @Override
        public AbstractSelectState getState() {
            return super.getState();
        }
    }
}
