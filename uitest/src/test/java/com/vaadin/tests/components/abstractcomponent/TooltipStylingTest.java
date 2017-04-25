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
package com.vaadin.tests.components.abstractcomponent;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserThemeTest;
import com.vaadin.tests.tb3.ParameterizedTB3Runner;
import com.vaadin.tests.tb3.SingleBrowserTest;

@RunWith(ParameterizedTB3Runner.class)
public class TooltipStylingTest extends SingleBrowserTest {

    private String theme;

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Parameters
    public static Collection<String> getThemes() {
        return MultiBrowserThemeTest.themesToTest;
    }

    @Test
    public void tooltipStyling() throws IOException {
        openTestURL("theme=" + theme);

        $(LabelElement.class).id("default").showTooltip();

        compareScreen("default");

        $(LabelElement.class).id("html").showTooltip();

        compareScreen("html");
    }
}
