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
package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 */
public class ComboBoxSuggestionPopupWidthLegacyTest extends MultiBrowserTest {

    @Test
    public void suggestionPopupLegacyWidthTest() throws Exception {
        openTestURL();

        waitForElementVisible(By.className("legacy"));

        WebElement selectTextbox = $(ComboBoxElement.class).first()
                .findElement(By.vaadin("#textbox"));
        selectTextbox.click();

        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        WebElement popup = cb.getSuggestionPopup();

        int width = popup.getSize().getWidth();
        assertGreater("Legacy mode popup should be quite wide", width, 600);
        assertLessThan(
                "Even legacy mode popup should not be over 1000px wide with the set item captions ",
                width, 1000);

    }

};
