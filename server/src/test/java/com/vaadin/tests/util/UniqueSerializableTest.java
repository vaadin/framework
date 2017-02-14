/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.tests.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;

import com.vaadin.ui.UniqueSerializable;

public class UniqueSerializableTest implements Serializable {

    @Test
    public void testUniqueness() {
        UniqueSerializable o1 = new UniqueSerializable() {
        };
        UniqueSerializable o2 = new UniqueSerializable() {
        };
        assertFalse(o1 == o2);
        assertFalse(o1.equals(o2));
    }

    @Test
    public void testSerialization() {
        UniqueSerializable o1 = new UniqueSerializable() {
        };
        UniqueSerializable d1 = (UniqueSerializable) SerializationUtils
                .deserialize(SerializationUtils.serialize(o1));
        assertTrue(d1.equals(o1));
    }

}
