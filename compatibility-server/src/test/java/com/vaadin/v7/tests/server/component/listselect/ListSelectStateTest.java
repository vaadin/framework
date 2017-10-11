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
package com.vaadin.v7.tests.server.component.listselect;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.v7.shared.ui.select.AbstractSelectState;
import com.vaadin.v7.ui.ListSelect;

/**
 * Tests for ListSelect State.
 *
 */
public class ListSelectStateTest {

    @Test
    public void getState_listSelectHasCustomState() {
        TestListSelect select = new TestListSelect();
        AbstractSelectState state = select.getState();
        assertEquals("Unexpected state class", AbstractSelectState.class,
                state.getClass());
    }

    private static class TestListSelect extends ListSelect {
        @Override
        public AbstractSelectState getState() {
            return super.getState();
        }
    }

}
