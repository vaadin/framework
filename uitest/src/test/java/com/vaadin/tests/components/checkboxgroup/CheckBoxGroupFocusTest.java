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
package com.vaadin.tests.components.checkboxgroup;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxGroupFocusTest extends MultiBrowserTest {

    @Test
    public void focusOnInit() {
        openTestURL();
        WebElement focused = getFocusedElement();
        assertNotNull(
                "No focused element found in RadioButtonGroup after initial focus()",
                focused);
        CheckBoxGroupElement checkBoxGroup = $(CheckBoxGroupElement.class)
                .first();
        Boolean isChild = (Boolean) executeScript(
                "return (arguments[0].querySelector(\"#gwt-uid-12\") == arguments[1]);",
                checkBoxGroup, focused);
        assertTrue("Focused element is in the first RadioButtonGroup", isChild);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Focus does not move when expected with Selenium/TB and Firefox 45
        return getBrowsersExcludingFirefox();
    }

}
