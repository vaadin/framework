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

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxPopupPositionEmbeddedInDivTest extends MultiBrowserTest {

    @Test
    public void popupBelow() {
        driver.get(StringUtils.strip(getBaseURL(), "/")
                + "/statictestfiles/ComboBoxEmbeddingHtmlPage.html");

        // Chrome requires document.scrollTop (<body>)
        // Firefox + IE wants document.documentElement.scrollTop (<html>)
        executeScript(
                "document.body.scrollTop=200;document.documentElement.scrollTop=200;document.body.scrollLeft=50;document.documentElement.scrollLeft=50;");

        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        combobox.openPopup();
        WebElement popup = $(ComboBoxElement.class).first()
                .getSuggestionPopup();

        Point comboboxLocation = combobox.getLocation();
        Point popupLocation = popup.getLocation();
        Assert.assertTrue("Popup should be below combobox",
                popupLocation.getY() > comboboxLocation.getY());

        Assert.assertTrue("Popup should be left aligned with the combobox",
                popupLocation.getX() == comboboxLocation.getX());
    }
}
