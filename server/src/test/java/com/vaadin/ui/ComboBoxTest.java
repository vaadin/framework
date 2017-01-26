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
package com.vaadin.ui;

import org.junit.Test;

import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.tests.util.MockUI;

public class ComboBoxTest {

    @Test
    public void testResetValue() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems("one", "two");

        // Reset value whenever it changes (in a real case, this listener would
        // do something with the selected value before discarding it)
        comboBox.addValueChangeListener(e -> comboBox.setValue(null));

        // "Attach" the component and initialize diffstate
        new MockUI().setContent(comboBox);
        ComponentTest.syncToClient(comboBox);

        // Emulate selection of "one"
        String oneKey = comboBox.getDataCommunicator().getKeyMapper()
                .key("one");
        ServerRpcManager.getRpcProxy(comboBox, SelectionServerRpc.class)
                .select(oneKey);

        ComponentTest.assertEncodedStateProperties(comboBox,
                "Selection change done by the listener should be sent to the client",
                "selectedItemKey");
    }
}
