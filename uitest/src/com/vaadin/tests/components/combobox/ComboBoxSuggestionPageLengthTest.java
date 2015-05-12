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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxSuggestionPageLengthTest extends MultiBrowserTest {

    @Test
    public void testSuggestionsPageLength0() {
        openTestURL();

        WebElement textboxPageLength0 = $(ComboBoxElement.class).first()
                .findElement(By.tagName("input"));
        textboxPageLength0.sendKeys("c");
        assertSuggestions("abc", "cde");
    }

    @Test
    public void testSuggestionsPageLength2() {
        openTestURL();

        WebElement textboxPageLength2 = $(ComboBoxElement.class).get(1)
                .findElement(By.tagName("input"));
        textboxPageLength2.sendKeys("e");
        assertSuggestions("cde", "efg");
    }

    private void assertSuggestions(String... expected) {
        assertEquals(Arrays.asList(expected), getSuggestionsOnScreen());
    }

    private List<String> getSuggestionsOnScreen() {
        List<WebElement> suggestionElements = getDriver()
                .findElements(
                        By.cssSelector(".v-filterselect-suggestpopup .gwt-MenuItem span"));

        List<String> suggestions = new ArrayList<String>();
        for (WebElement suggestion : suggestionElements) {
            suggestions.add(suggestion.getText());
        }
        return suggestions;
    }

}
