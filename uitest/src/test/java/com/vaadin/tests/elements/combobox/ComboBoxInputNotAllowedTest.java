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
package com.vaadin.tests.elements.combobox;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxInputNotAllowedTest extends MultiBrowserTest {

    @Test
    @Ignore("Build got stuck on this, so temporarily disabled")
    public void selectByTextComboBoxWithTextInputDisabled_invalidSelection() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.selectByText("Foobar");
    }

    @Test
    public void selectByTextComboBoxWithTextInputDisabled() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();

        String[] optionsToTest = new String[] {
                ComboBoxInputNotAllowed.ITEM_ON_FIRST_PAGE,
                ComboBoxInputNotAllowed.ITEM_ON_SECOND_PAGE,
                ComboBoxInputNotAllowed.ITEM_ON_LAST_PAGE,
                ComboBoxInputNotAllowed.ITEM_LAST_WITH_PARENTHESIS,
                ComboBoxInputNotAllowed.ITEM_ON_FIRST_PAGE };

        for (String option : optionsToTest) {
            cb.selectByText(option);
            Assert.assertEquals("Value is now: " + option,
                    $(LabelElement.class).last().getText());
            Assert.assertEquals(option, cb.getValue());
        }
    }
}
