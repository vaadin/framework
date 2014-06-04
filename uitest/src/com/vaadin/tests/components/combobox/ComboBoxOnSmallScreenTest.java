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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebElement;

import com.vaadin.client.ui.VFilterSelect;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * ComboBox suggestion popup should not obscure the text input box.
 * 
 * @author Vaadin Ltd
 */
public class ComboBoxOnSmallScreenTest extends MultiBrowserTest {

    private static final Dimension TARGETSIZE = new Dimension(600, 300);
    private static final String POPUPCLASSNAME = VFilterSelect.CLASSNAME
            + "-suggestpopup";

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();

        getWindow().setSize(TARGETSIZE);
    }

    @Test
    public void testUpperSuggestionPopupOverlayPosition() {
        ComboBoxElement cb = getComboBoxAndOpenPopup(0);
        assertOverlayPosition(cb, getPopup());
    }

    @Test
    public void testUpperSuggestionPopupOverlaySize() {
        ComboBoxElement cb = getComboBoxAndOpenPopup(0);
        assertOverlaySize(cb, getPopup());
    }

    @Test
    public void testLowerSuggestionPopupOverlayPosition() {
        ComboBoxElement cb = getComboBoxAndOpenPopup(1);
        assertOverlayPosition(cb, getPopup());
    }

    @Test
    public void testLowerSuggestionPopupOverlaySize() {
        ComboBoxElement cb = getComboBoxAndOpenPopup(1);
        assertOverlaySize(cb, getPopup());
    }

    private void assertOverlayPosition(WebElement combobox, WebElement popup) {
        final int popupTop = popup.getLocation().y;
        final int popupBottom = popupTop + popup.getSize().getHeight();
        final int cbTop = combobox.getLocation().y;
        final int cbBottom = cbTop + combobox.getSize().getHeight();

        assertThat("Popup overlay does not overlap with the textbox",
                popupTop >= cbBottom || popupBottom <= cbTop, is(true));
    }

    private void assertOverlaySize(WebElement combobox, WebElement popup) {
        final int popupTop = popup.getLocation().y;
        final int popupBottom = popupTop + popup.getSize().getHeight();
        final int rootHeight = findElement(By.tagName("body")).getSize().height;

        assertThat("Popup overlay inside the viewport", popupTop < 0
                || popupBottom > rootHeight, is(false));
    }

    private ComboBoxElement getComboBoxAndOpenPopup(int comboboxIndex) {
        ComboBoxElement cb = $(ComboBoxElement.class).get(comboboxIndex);
        cb.openPopup();
        return cb;
    }

    private WebElement getPopup() {
        return findElement(By.className(POPUPCLASSNAME));
    }

    private Window getWindow() {
        return getDriver().manage().window();
    }

}
