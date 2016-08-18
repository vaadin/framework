/*
 * Copyright 2000-2014 Vaadin Ltd.
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
