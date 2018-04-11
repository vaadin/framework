package com.vaadin.v7.tests.server.component.optiongroup;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.v7.shared.ui.optiongroup.OptionGroupState;
import com.vaadin.v7.ui.OptionGroup;

/**
 * Tests for OptionGroup state.
 *
 */
public class OptionGroupStateTest {

    @Test
    public void getState_optionGroupHasCustomState() {
        TestOptionGroup group = new TestOptionGroup();
        OptionGroupState state = group.getState();
        assertEquals("Unexpected state class", OptionGroupState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_optionGroupHasCustomPrimaryStyleName() {
        OptionGroup layout = new OptionGroup();
        OptionGroupState state = new OptionGroupState();
        assertEquals("Unexpected primary style name", state.primaryStyleName,
                layout.getPrimaryStyleName());
    }

    @Test
    public void optionGroupStateHasCustomPrimaryStyleName() {
        OptionGroupState state = new OptionGroupState();
        assertEquals("Unexpected primary style name", "v-select-optiongroup",
                state.primaryStyleName);
    }

    private static class TestOptionGroup extends OptionGroup {

        @Override
        public OptionGroupState getState() {
            return super.getState();
        }
    }
}
