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
package com.vaadin.tests.elements.nativeselect;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NativeSelectSetValueTest extends MultiBrowserTest {

    NativeSelectElement select;
    LabelElement counter;

    @Before
    public void init() {
        openTestURL();
        select = $(NativeSelectElement.class).get(0);
        counter = $(LabelElement.class).id("counter");
    }

    @Test
    public void testSetValue() throws InterruptedException {
        select.setValue("item 2");
        checkTestValue();
    }

    @Test
    public void testSelectByText() {
        select.selectByText("item 2");
        checkTestValue();
    }

    private void checkTestValue() {
        // checks value has changed
        String actual = select.getValue();
        Assert.assertEquals("item 2", actual);
        // checks change value event occures
        Assert.assertEquals("1", counter.getText());
    }
}
