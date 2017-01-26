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

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TextFieldConstructorTest {

    @Test
    public void initiallyEmpty() {
        TextField textField = new TextField();
        Assert.assertTrue(textField.isEmpty());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        TextField textField = new TextField(null, "foobar");
        Assert.assertFalse(textField.isEmpty());

        textField.clear();
        Assert.assertTrue(textField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        AtomicInteger eventCounter = new AtomicInteger(0);
        TextField textField = new TextField(event -> eventCounter.incrementAndGet());

        assertEquals(0, eventCounter.get());

        textField.setValue("value change");

        assertEquals(1, eventCounter.get());
    }

    @Test
    public void testCaptionValueListener() {
        AtomicInteger eventCounter = new AtomicInteger(0);
        TextField textField = new TextField("Caption", "Initial value", event -> eventCounter.incrementAndGet());

        assertEquals(0, eventCounter.get());

        textField.setValue("value change");

        assertEquals(1, eventCounter.get());
    }
}
