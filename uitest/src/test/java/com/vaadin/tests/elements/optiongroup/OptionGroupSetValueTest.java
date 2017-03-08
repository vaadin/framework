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
package com.vaadin.tests.elements.optiongroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class OptionGroupSetValueTest extends MultiBrowserTest {

    private static final String NEW_VALUE = "item2";

    private OptionGroupElement group;

    @Before
    public void init() {
        openTestURL();
        group = $(OptionGroupElement.class).first();
    }

    @Test
    public void testSetValue() {
        group.setValue(NEW_VALUE);
        Assert.assertEquals(NEW_VALUE, group.getValue());
    }

    @Test
    public void testSelectByText() {
        group.selectByText(NEW_VALUE);
        Assert.assertEquals(NEW_VALUE, group.getValue());
    }

}
