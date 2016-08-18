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
