package com.vaadin.tests.server.component.abstractselect;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.select.AbstractSelectState;
import com.vaadin.ui.AbstractSelect;

/**
 * Tests for AbstractSelect state
 *
 */
public class AbstractSelectStateTest {

    @Test
    public void getState_selectHasCustomState() {
        TestSelect select = new TestSelect();
        AbstractSelectState state = select.getState();
        Assert.assertEquals("Unexpected state class", AbstractSelectState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_selectHasCustomPrimaryStyleName() {
        TestSelect combobox = new TestSelect();
        AbstractSelectState state = new AbstractSelectState();
        Assert.assertEquals("Unexpected primary style name",
                state.primaryStyleName, combobox.getPrimaryStyleName());
    }

    @Test
    public void selectStateHasCustomPrimaryStyleName() {
        AbstractSelectState state = new AbstractSelectState();
        Assert.assertEquals("Unexpected primary style name", "v-select",
                state.primaryStyleName);
    }

    private static class TestSelect extends AbstractSelect {

        @Override
        public AbstractSelectState getState() {
            return super.getState();
        }
    }
}
