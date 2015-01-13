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
package com.vaadin.ui;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.IndexedContainer;

public class NativeSelectTest {

    @Test
    public void rpcRegisteredConstructorNoArg() {
        assertFocusRpcRegistered(new NativeSelect());
    }

    @Test
    public void rpcRegisteredConstructorString() {
        assertFocusRpcRegistered(new NativeSelect("foo"));
    }

    @Test
    public void rpcRegisteredConstructorStringCollection() {
        assertFocusRpcRegistered(new NativeSelect("foo",
                Collections.singleton("Hello")));
    }

    @Test
    public void rpcRegisteredConstructorStringContainer() {
        assertFocusRpcRegistered(new NativeSelect("foo", new IndexedContainer()));
    }

    private void assertFocusRpcRegistered(NativeSelect s) {
        Assert.assertNotNull(
                "RPC is not correctly registered",
                s.getRpcManager("com.vaadin.shared.communication.FieldRpc$FocusAndBlurServerRpc"));
    }

}
