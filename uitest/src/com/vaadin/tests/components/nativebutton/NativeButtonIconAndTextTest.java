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
package com.vaadin.tests.components.nativebutton;

import static com.vaadin.tests.components.nativebutton.NativeButtonIconAndText.BUTTON_TEXT;
import static com.vaadin.tests.components.nativebutton.NativeButtonIconAndText.BUTTON_TEXT_ICON;
import static com.vaadin.tests.components.nativebutton.NativeButtonIconAndText.BUTTON_TEXT_ICON_ALT;
import static com.vaadin.tests.components.nativebutton.NativeButtonIconAndText.INITIAL_ALTERNATE_TEXT;
import static com.vaadin.tests.components.nativebutton.NativeButtonIconAndText.NATIVE_BUTTON_TEXT;
import static com.vaadin.tests.components.nativebutton.NativeButtonIconAndText.NATIVE_BUTTON_TEXT_ICON;
import static com.vaadin.tests.components.nativebutton.NativeButtonIconAndText.NATIVE_BUTTON_TEXT_ICON_ALT;
import static com.vaadin.tests.components.nativebutton.NativeButtonIconAndText.UPDATED_ALTERNATE_TEXT;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class NativeButtonIconAndTextTest extends MultiBrowserTest {

    @Test
    public void testNativeButtonIconAltText() {
        openTestURL();
        assertAltText(BUTTON_TEXT, "");
        assertAltText(BUTTON_TEXT_ICON, "");
        assertAltText(BUTTON_TEXT_ICON_ALT, INITIAL_ALTERNATE_TEXT);
        assertAltText(NATIVE_BUTTON_TEXT, "");
        assertAltText(NATIVE_BUTTON_TEXT_ICON, "");
        assertAltText(NATIVE_BUTTON_TEXT_ICON_ALT, INITIAL_ALTERNATE_TEXT);

        clickElements(BUTTON_TEXT, BUTTON_TEXT_ICON, BUTTON_TEXT_ICON_ALT,
                NATIVE_BUTTON_TEXT, NATIVE_BUTTON_TEXT_ICON,
                NATIVE_BUTTON_TEXT_ICON_ALT);

        // Button without icon - should not get alt text
        assertAltText(BUTTON_TEXT, "");
        assertAltText(BUTTON_TEXT_ICON, UPDATED_ALTERNATE_TEXT);
        assertAltText(BUTTON_TEXT_ICON_ALT, "");
        // Button without icon - should not get alt text
        assertAltText(NATIVE_BUTTON_TEXT, "");
        assertAltText(NATIVE_BUTTON_TEXT_ICON, UPDATED_ALTERNATE_TEXT);
        assertAltText(NATIVE_BUTTON_TEXT_ICON_ALT, "");

    }

    private void clickElements(String... ids) {
        for (String id : ids) {
            vaadinElementById(id).click();
        }
    }

    /**
     * If the button identified by 'buttonId' has an icon, asserts that the
     * alternate text of the icon matches 'expected'. "" and null are considered
     * equivalent.
     * 
     * @param buttonId
     *            the id of the button who possibly contains an icon
     * @param expected
     *            the expected alternate text, cannot be null
     */
    private void assertAltText(String buttonId, String expected) {
        WebElement button = vaadinElementById(buttonId);
        List<WebElement> imgList = button.findElements(By.xpath(".//img"));
        if (imgList.isEmpty()) {
            return;
        }
        WebElement img = imgList.get(0);
        String alt = img.getAttribute("alt");
        if (alt == null) {
            alt = "";
        }

        Assert.assertEquals(expected, alt);

    }
}
