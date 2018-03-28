package com.vaadin.tests.server.component.twincolselect;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.twincolselect.TwinColSelectState;
import com.vaadin.ui.TwinColSelect;

/**
 * Tests for TwinColSelectState.
 *
 */
public class TwinColSelectStateTest {

    @Test
    public void getState_selectHasCustomState() {
        TestTwinColSelect select = new TestTwinColSelect();
        TwinColSelectState state = select.getState();
        Assert.assertEquals("Unexpected state class", TwinColSelectState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_selectHasCustomPrimaryStyleName() {
        TwinColSelect table = new TwinColSelect();
        TwinColSelectState state = new TwinColSelectState();
        Assert.assertEquals("Unexpected primary style name",
                state.primaryStyleName, table.getPrimaryStyleName());
    }

    @Test
    public void selectStateHasCustomPrimaryStyleName() {
        TwinColSelectState state = new TwinColSelectState();
        Assert.assertEquals("Unexpected primary style name", "v-select-twincol",
                state.primaryStyleName);
    }

    private static class TestTwinColSelect extends TwinColSelect {

        @Override
        public TwinColSelectState getState() {
            return super.getState();
        }
    }
}
