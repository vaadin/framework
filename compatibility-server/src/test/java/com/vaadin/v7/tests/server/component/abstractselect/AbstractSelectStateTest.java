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
