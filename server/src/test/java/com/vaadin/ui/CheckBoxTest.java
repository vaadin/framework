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

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.checkbox.CheckBoxServerRpc;
import com.vaadin.tests.util.MockUI;

public class CheckBoxTest {
    @Test
    public void initiallyFalse() {
        CheckBox cb = new CheckBox();
        Assert.assertFalse(cb.getValue());
    }

    @Test
    public void testSetValue() {
        CheckBox cb = new CheckBox();
        cb.setValue(true);
        Assert.assertTrue(cb.getValue());
        cb.setValue(false);
        Assert.assertFalse(cb.getValue());
    }

    @Test
    public void setValueChangeFromClientIsUserOriginated() {
        UI ui = new MockUI();
        CheckBox cb = new CheckBox();
        ui.setContent(cb);
        AtomicBoolean userOriginated = new AtomicBoolean(false);
        cb.addValueChangeListener(e -> {
            userOriginated.set(e.isUserOriginated());
        });
        ComponentTest.syncToClient(cb);
        ServerRpcManager.getRpcProxy(cb, CheckBoxServerRpc.class)
                .setChecked(true, new MouseEventDetails());
        Assert.assertTrue(userOriginated.get());
        userOriginated.set(false);
        ComponentTest.syncToClient(cb);
        ServerRpcManager.getRpcProxy(cb, CheckBoxServerRpc.class)
                .setChecked(false, new MouseEventDetails());
        Assert.assertTrue(userOriginated.get());
    }

    @Test
    public void setValueChangeFromServerIsNotUserOriginated() {
        UI ui = new MockUI();
        CheckBox cb = new CheckBox();
        ui.setContent(cb);
        AtomicBoolean userOriginated = new AtomicBoolean(true);
        cb.addValueChangeListener(e -> {
            userOriginated.set(e.isUserOriginated());
        });
        cb.setValue(true);
        Assert.assertFalse(userOriginated.get());
        userOriginated.set(true);
        cb.setValue(false);
        Assert.assertFalse(userOriginated.get());
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_throwsNPE() {
        CheckBox cb = new CheckBox();
        cb.setValue(null);
    }

}
