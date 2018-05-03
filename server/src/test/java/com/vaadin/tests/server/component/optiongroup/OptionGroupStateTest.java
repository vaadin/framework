package com.vaadin.tests.server.component.optiongroup;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.optiongroup.OptionGroupState;
import com.vaadin.ui.OptionGroup;

/**
 * Tests for OptionGroup state.
 *
 */
public class OptionGroupStateTest {

    @Test
    public void getState_optionGroupHasCustomState() {
        TestOptionGroup group = new TestOptionGroup();
        OptionGroupState state = group.getState();
        Assert.assertEquals("Unexpected state class", OptionGroupState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_optionGroupHasCustomPrimaryStyleName() {
        OptionGroup layout = new OptionGroup();
        OptionGroupState state = new OptionGroupState();
        Assert.assertEquals("Unexpected primary style name",
                state.primaryStyleName, layout.getPrimaryStyleName());
    }

    @Test
    public void optionGroupStateHasCustomPrimaryStyleName() {
        OptionGroupState state = new OptionGroupState();
        Assert.assertEquals("Unexpected primary style name",
                "v-select-optiongroup", state.primaryStyleName);
    }

    private static class TestOptionGroup extends OptionGroup {

        @Override
        public OptionGroupState getState() {
            return super.getState();
        }
    }
}
