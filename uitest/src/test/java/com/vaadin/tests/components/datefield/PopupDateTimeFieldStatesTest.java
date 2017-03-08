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
package com.vaadin.tests.components.datefield;

import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PopupDateTimeFieldStatesTest extends MultiBrowserTest {

    @Test
    public void readOnlyDateFieldPopupShouldNotOpen()
            throws IOException, InterruptedException {
        openTestURL();

        // wait until loading indicator becomes invisible
        WebElement loadingIndicator = findElement(
                By.className("v-loading-indicator"));
        Pattern pattern = Pattern.compile("display: *none;");
        waitUntil(driver -> {
            return pattern.matcher(loadingIndicator.getAttribute("style"))
                    .find();
        });

        compareScreen("dateFieldStates");
    }

}
