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
package com.vaadin.tests.components.grid;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vaadin Ltd
 */
public class GridAriaMultiselectableTest extends SingleBrowserTest {

    @Test
    public void checkAriaMultiselectable() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        Assert.assertTrue("Grid should have the role 'grid'",
                grid.getHTML().contains("role=\"grid\""));
        Assert.assertFalse("Grid should not have aria-multiselectable",
                grid.getHTML().contains("aria-multiselectable"));

        $(ButtonElement.class).caption("SingleSelect").first().click();

        Assert.assertTrue("Grid should have aria-multiselectable 'false'",
                grid.getHTML().contains("aria-multiselectable=\"false\""));

        $(ButtonElement.class).caption("MultiSelect").first().click();

        Assert.assertTrue("Grid should have aria-multiselectable 'true'",
                grid.getHTML().contains("aria-multiselectable=\"true\""));
    }
}
