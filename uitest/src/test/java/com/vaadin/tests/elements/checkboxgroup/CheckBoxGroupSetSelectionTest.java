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
package com.vaadin.tests.elements.checkboxgroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxGroupSetSelectionTest extends MultiBrowserTest {

    private static final String NEW_VALUE = "item2";

    private CheckBoxGroupElement group;

    @Before
    public void init() {
        openTestURL();
        group = $(CheckBoxGroupElement.class).first();

    }

    @Test
    public void testSetSelection() {
        group.setValue(NEW_VALUE);
        Assert.assertEquals(Collections.singletonList(NEW_VALUE),
                group.getValue());
    }

    @Test
    public void testSelectByText() {
        group.selectByText(NEW_VALUE);
        Assert.assertEquals(Collections.singletonList(NEW_VALUE),
                group.getValue());
    }

    @Test
    public void testSelectAll() {
        List<String> value = Arrays.asList("item1", "item2", "item3");
        group.setValue(value);
        Assert.assertEquals(value, group.getValue());
    }

    @Test
    public void testSelectNone() {
        testSelectByText();

        group.setValue();
        Assert.assertEquals(Collections.emptyList(), group.getValue());
    }

    @Test
    public void testDeselect() {
        testSelectAll();
        testSetSelection();
    }

}
