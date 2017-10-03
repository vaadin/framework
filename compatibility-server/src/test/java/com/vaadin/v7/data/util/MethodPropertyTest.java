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
package com.vaadin.v7.data.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.util.NestedMethodPropertyTest.Address;

public class MethodPropertyTest {

    private Address testObject;

    @Before
    public void setup() {
        testObject = new Address("some street", 123);
    }

    @Test
    public void getValue() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject,
                "street");
        assertEquals("some street", mp.getValue());
    }

    @Test
    public void getValueAfterBeanUpdate() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject,
                "street");
        testObject.setStreet("Foo street");
        assertEquals("Foo street", mp.getValue());
    }

    @Test
    public void setValue() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject,
                "street");
        mp.setValue("Foo street");
        assertEquals("Foo street", testObject.getStreet());
    }

    @Test
    public void changeInstance() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject,
                "street");
        Address newStreet = new Address("new street", 999);
        mp.setInstance(newStreet);
        assertEquals("new street", mp.getValue());
        assertEquals("some street", testObject.getStreet());

    }

    @Test(expected = IllegalArgumentException.class)
    public void changeInstanceToIncompatible() {
        MethodProperty<String> mp = new MethodProperty<String>(testObject,
                "street");
        mp.setInstance("foobar");

    }

}
