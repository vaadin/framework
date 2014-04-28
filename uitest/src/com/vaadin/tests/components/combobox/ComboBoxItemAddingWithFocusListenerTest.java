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
package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxItemAddingWithFocusListenerTest extends MultiBrowserTest {

    private final String comboBoxSelector = "/VVerticalLayout[0]/VVerticalLayout[0]/VFilterSelect[0]";

    @Test
    public void testPopupViewContainsAddedItem() {
        openTestURL();
        WebElement cbTextbox = driver.findElement(By.vaadin(comboBoxSelector
                + "#textbox"));
        WebElement focusTarget = driver.findElement(By
                .vaadin("/VVerticalLayout[0]/VVerticalLayout[0]/VButton[0]"));
        driver.findElement(By.vaadin(comboBoxSelector + "#button")).click();
        int i = 0;
        while (i < 3) {
            assertTrue("No item added on focus", getPopupSuggestions()
                    .contains("Focus" + i++));
            focus(focusTarget);
            focus(cbTextbox);
        }
        assertTrue("No item added on focus",
                getPopupSuggestions().contains("Focus" + i));
    }

    /**
     * @param focusTarget
     *            Element to focus
     */
    private void focus(WebElement focusTarget) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;

        Object ret = jse.executeScript(
                "try { arguments[0].focus() } catch(e) {}; return null;",
                focusTarget);
    }

    /**
     * @return List of Suggestion in Popup
     */
    private List<String> getPopupSuggestions() {
        List<String> suggestionsTexts = new ArrayList<String>();
        List<WebElement> suggestions = driver.findElement(
                By.vaadin(comboBoxSelector + "#popup")).findElements(
                By.tagName("span"));
        for (WebElement suggestion : suggestions) {
            String text = suggestion.getText();
            if (!text.isEmpty()) {
                suggestionsTexts.add(text);
            }
        }
        return suggestionsTexts;
    }
}
