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
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vaadin Ltd
 */
public class GridAriaMultiselectableTest extends MultiBrowserTest {

    @Test
    public void checkAriaMultiselectable() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        Assert.assertEquals("Grid should have the role 'grid'",
                "grid", grid.getAttribute("role"));
        Assert.assertEquals("Grid should not have the aria-multiselectable",
                null, grid.getAttribute("aria-multiselectable"));

        $(ButtonElement.class).caption("SingleSelect").first().click();

        Assert.assertEquals("Grid should have aria-multiselectable 'false'",
                "false", grid.getAttribute("aria-multiselectable"));

        $(ButtonElement.class).caption("MultiSelect").first().click();

        Assert.assertEquals("Grid should have aria-multiselectable 'true'",
                "true", grid.getAttribute("aria-multiselectable"));
    }
}
