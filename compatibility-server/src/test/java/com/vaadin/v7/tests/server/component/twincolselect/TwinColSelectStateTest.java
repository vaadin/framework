package com.vaadin.v7.tests.server.component.twincolselect;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.v7.shared.ui.twincolselect.TwinColSelectState;
import com.vaadin.v7.ui.TwinColSelect;

/**
 * Tests for TwinColSelectState.
 *
 */
public class TwinColSelectStateTest {

    @Test
    public void getState_selectHasCustomState() {
        TestTwinColSelect select = new TestTwinColSelect();
        TwinColSelectState state = select.getState();
        assertEquals("Unexpected state class", TwinColSelectState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_selectHasCustomPrimaryStyleName() {
        TwinColSelect table = new TwinColSelect();
        TwinColSelectState state = new TwinColSelectState();
        assertEquals("Unexpected primary style name", state.primaryStyleName,
                table.getPrimaryStyleName());
    }

    @Test
    public void selectStateHasCustomPrimaryStyleName() {
        TwinColSelectState state = new TwinColSelectState();
        assertEquals("Unexpected primary style name", "v-select-twincol",
                state.primaryStyleName);
    }

    private static class TestTwinColSelect extends TwinColSelect {

        @Override
        public TwinColSelectState getState() {
            return super.getState();
        }
    }
}
