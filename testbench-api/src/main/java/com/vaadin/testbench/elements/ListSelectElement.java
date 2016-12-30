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
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.ListSelect")
public class ListSelectElement extends AbstractSelectElement {

    private Select select;
    private static By bySelect = By.tagName("select");
    private WebElement selectElement;

    @Override
    protected void init() {
        super.init();
        selectElement = findElement(bySelect);
        select = new Select(selectElement);
    }

    /**
     * Selects the option with the given text.
     * <p>
     * For a ListSelect in multi select mode, adds the given option(s) to the
     * current selection.
     *
     * @param text
     *            the text of the option
     */
    public void selectByText(String text) {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }

        select.selectByVisibleText(text);
        if (isPhantomJS() && select.isMultiple()) {
            // Phantom JS does not fire a change event when
            // selecting/deselecting items in a multi select
            fireChangeEvent(selectElement);
        }
    }

    /**
     * Deselects the option(s) with the given text.
     *
     * @param text
     *            the text of the option
     */
    public void deselectByText(String text) {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        select.deselectByVisibleText(text);
        if (isPhantomJS() && select.isMultiple()) {
            // Phantom JS does not fire a change event when
            // selecting/deselecting items in a multi select
            fireChangeEvent(selectElement);
        }
    }

    /**
     * Gets a list of the texts shown for all options.
     *
     * @return a list of option texts
     */
    public List<String> getOptions() {
        List<String> options = new ArrayList<String>();
        for (WebElement webElement : select.getOptions()) {
            options.add(webElement.getText());
        }
        return options;
    }

    /**
     * Clear operation is not supported for List Select. This operation has no
     * effect on List Select element.
     */
    @Override
    public void clear() {
        super.clear();
    }

    /**
     * Return value of the list select element
     *
     * @return value of the list select element
     */
    public String getValue() {
        return select.getFirstSelectedOption().getText();
    }

    private void fireChangeEvent(WebElement target) {
        if (!(getDriver() instanceof JavascriptExecutor)) {
            return;
        }

        ((JavascriptExecutor) getDriver()).executeScript(
                "var ev = document.createEvent('HTMLEvents');" //
                        + "ev.initEvent('change', false, true);" //
                        + "arguments[0].dispatchEvent(ev);", //
                target);

    }

}
