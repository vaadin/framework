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
package com.vaadin.v7.tests.server.component.optiongroup;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.optiongroup.OptionGroupState;
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
