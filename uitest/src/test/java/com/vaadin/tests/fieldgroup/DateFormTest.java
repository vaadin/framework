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
package com.vaadin.tests.fieldgroup;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.InlineDateFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFormTest extends MultiBrowserTest {

    @Test
    public void testCorrectDateFormat() throws Exception {
        openTestURL();
        Assert.assertEquals("Unexpected DateField value,", "1/20/84",
                getDateFieldValue());
        Assert.assertEquals("Unexpected PopupDateField value,", "1/21/84",
                getPopupDateFieldValue());
        WebElement day20 = getInlineDateFieldCalendarPanel()
                .findElement(By.vaadin("#day20"));
        Assert.assertTrue(
                "Unexpected InlineDateField state, 20th not selected.",
                hasCssClass(day20,
                        "v-inline-datefield-calendarpanel-day-selected"));
        Assert.assertEquals("Unexpected TextField contents,",
                "Jan 20, 1984 4:34:49 PM",
                $(TextFieldElement.class).first().getValue());
    }

    protected String getDateFieldValue() {
        return $(DateFieldElement.class).first().getValue();
    }

    protected String getPopupDateFieldValue() {
        return $(DateFieldElement.class).get(1).getValue();
    }

    protected WebElement getInlineDateFieldCalendarPanel() {
        return $(InlineDateFieldElement.class).first()
                .findElement(By.className("v-inline-datefield-calendarpanel"));
    }

}
