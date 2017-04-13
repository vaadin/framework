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
package com.vaadin.testbench.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.ComboBox")
public class ComboBoxElement extends AbstractSelectElement {

    private static org.openqa.selenium.By bySuggestionPopup = By
            .vaadin("#popup");
    private static org.openqa.selenium.By byNextPage = By
            .className("v-filterselect-nextpage");
    private static org.openqa.selenium.By byPrevPage = By
            .className("v-filterselect-prevpage");

    /**
     * Selects the first option in the ComboBox which matches the given text.
     *
     * @param text
     *            the text of the option to select
     */
    public void selectByText(String text) {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }

        if (!isTextInputAllowed()) {
            selectByTextFromPopup(text);
            return;
        }
        getInputField().clear();
        getInputField().sendKeys(text);

        selectSuggestion(text);
    }

    /**
     * Selects, without filtering, the first option in the ComboBox which
     * matches the given text.
     *
     * @param text
     *            the text of the option to select
     */
    private void selectByTextFromPopup(String text) {
        // This method assumes there is no need to touch the filter string

        // 1. Find first page
        // 2. Select first matching text if found
        // 3. Iterate towards end

        while (openPrevPage()) {
            // Scroll until beginning
        }

        do {
            if (selectSuggestion(text)) {
                return;
            }
        } while (openNextPage());
    }

    private boolean selectSuggestion(String text) {
        for (WebElement suggestion : getPopupSuggestionElements()) {
            if (text.equals(suggestion.getText())) {
                clickElement(suggestion);
                return true;
            }
        }
        return false;
    }

    private boolean isReadOnly(WebElement elem) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        return (Boolean) js.executeScript("return arguments[0].readOnly", elem);
    }

    /**
     * Checks if text input is allowed for the combo box.
     *
     * @return <code>true</code> if text input is allowed, <code>false</code>
     *         otherwise
     */
    public boolean isTextInputAllowed() {
        return !isReadOnly(getInputField());
    }

    /**
     * Checks whether the suggestion popup is open or not.
     *
     * @return {@code true} if popup is open, {@code false if not}
     */
    public boolean isPopupOpen() {
        return isElementPresent(bySuggestionPopup);
    }

    /**
     * Open the suggestion popup
     */
    public void openPopup() {
        findElement(By.vaadin("#button")).click();
    }

    /**
     * Gets the text representation of all suggestions on the current page
     *
     * @return List of suggestion texts
     */
    public List<String> getPopupSuggestions() {
        List<String> suggestionsTexts = new ArrayList<>();
        List<WebElement> suggestions = getPopupSuggestionElements();
        for (WebElement suggestion : suggestions) {
            String text = suggestion.getText();
            if (!text.isEmpty()) {
                suggestionsTexts.add(text);
            }
        }
        return suggestionsTexts;
    }

    /**
     * Gets the elements of all suggestions on the current page.
     * <p>
     * Opens the popup if not already open.
     *
     * @return a list of elements for the suggestions on the current page
     */
    public List<WebElement> getPopupSuggestionElements() {
        List<WebElement> tables = getSuggestionPopup()
                .findElements(By.tagName("table"));
        if (tables == null || tables.isEmpty()) {
            return Collections.emptyList();
        }
        WebElement table = tables.get(0);
        return table.findElements(By.tagName("td"));
    }

    /**
     * Opens next popup page.
     *
     * @return True if next page opened. false if doesn't have next page
     */
    public boolean openNextPage() {
        try {
            clickElement(getSuggestionPopup().findElement(byNextPage));
            return true;
        } catch (WebDriverException e) {
            // PhantomJS driver can throw WDE instead of the more specific
            // NoSuchElementException
            return false;
        }
    }

    /**
     * Open previous popup page.
     *
     * @return True if previous page opened. False if doesn't have previous page
     */
    public boolean openPrevPage() {
        try {
            clickElement(getSuggestionPopup().findElement(byPrevPage));
            return true;
        } catch (WebDriverException e) {
            // PhantomJS driver can throw WDE instead of the more specific
            // NoSuchElementException
            return false;
        }
    }

    /**
     * Returns the suggestion popup element
     */
    public WebElement getSuggestionPopup() {
        ensurePopupOpen();
        return findElement(bySuggestionPopup);
    }

    /**
     * Return value of the combo box element
     *
     * @return value of the combo box element
     */
    public String getValue() {
        return getInputField().getAttribute("value");
    }

    /**
     * Returns the text input field element, used for entering text into the
     * combo box.
     *
     * @return the input field element
     */
    public WebElement getInputField() {
        return findElement(By.vaadin("#textbox"));
    }

    private void ensurePopupOpen() {
        if (!isElementPresent(bySuggestionPopup)) {
            openPopup();
        }
    }

    @Override
    public String getText() {
        return getInputField().getAttribute("value");
    }

    @Override
    public void clear() {
        getInputField().clear();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        sendKeys(50, keysToSend);
    }

    /**
     * Use this method to simulate typing into an element, which may set its
     * value.
     *
     * @param delay
     *            delay after sending each individual key (mainly needed for
     *            PhantomJS)
     * @param keysToSend
     *            keys to type into the element
     */
    public void sendKeys(int delay, CharSequence... keysToSend) {
        WebElement input = getInputField();

        for (CharSequence key : keysToSend) {
            input.sendKeys(key);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
        }
    }

    private void clickElement(WebElement element) {
        if (isFirefox()) {
            // Workaround for Selenium/TB and Firefox 45 issue
            ((TestBenchElement) element).clickHiddenElement();
        } else {
            element.click();
        }
    }
}
