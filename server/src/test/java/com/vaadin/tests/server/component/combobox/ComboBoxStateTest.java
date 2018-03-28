package com.vaadin.tests.server.component.combobox;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.combobox.ComboBoxState;
import com.vaadin.ui.ComboBox;

/**
 * Tests for ComboBox state.
 *
 */
public class ComboBoxStateTest {
    @Test
    public void getState_comboboxHasCustomState() {
        TestComboBox combobox = new TestComboBox();
        ComboBoxState state = combobox.getState();
        Assert.assertEquals("Unexpected state class", ComboBoxState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_comboboxHasCustomPrimaryStyleName() {
        ComboBox combobox = new ComboBox();
        ComboBoxState state = new ComboBoxState();
        Assert.assertEquals("Unexpected primary style name",
                state.primaryStyleName, combobox.getPrimaryStyleName());
    }

    @Test
    public void comboboxStateHasCustomPrimaryStyleName() {
        ComboBoxState state = new ComboBoxState();
        Assert.assertEquals("Unexpected primary style name", "v-filterselect",
                state.primaryStyleName);
    }

    private static class TestComboBox extends ComboBox {

        @Override
        public ComboBoxState getState() {
            return super.getState();
        }
    }
}
