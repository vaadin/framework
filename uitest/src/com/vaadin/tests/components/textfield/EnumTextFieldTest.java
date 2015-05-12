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
package com.vaadin.tests.components.textfield;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class EnumTextFieldTest extends SingleBrowserTest {
    @Test
    public void validValues() {
        openTestURL();
        $(TextFieldElement.class).first().clear();
        $(TextFieldElement.class).first().sendKeys("Value", Keys.TAB);
        Assert.assertEquals("3. Value (valid)", getLogRow(0));

        $(TextFieldElement.class).first().clear();
        $(TextFieldElement.class).first().sendKeys("VaLuE");
        $(TextFieldElement.class).first().sendKeys(Keys.TAB);
        Assert.assertEquals("5. Value (valid)", getLogRow(0));

        $(TextFieldElement.class).first().clear();
        $(TextFieldElement.class).first().sendKeys("The last value");
        $(TextFieldElement.class).first().sendKeys(Keys.TAB);
        Assert.assertEquals("7. The last value (valid)", getLogRow(0));

        $(TextFieldElement.class).first().clear();
        Assert.assertEquals("8. null (valid)", getLogRow(0));

    }

    @Test
    public void invalidValue() {
        openTestURL();
        $(TextFieldElement.class).first().clear();

        $(TextFieldElement.class).first().sendKeys("bar");
        $(TextFieldElement.class).first().sendKeys(Keys.TAB);
        Assert.assertEquals("3. bar (INVALID)", getLogRow(0));

    }
}
