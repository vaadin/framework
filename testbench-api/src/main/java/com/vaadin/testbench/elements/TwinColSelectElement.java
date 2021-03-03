/*
 * Copyright 2000-2021 Vaadin Ltd.
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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.TwinColSelect")
public class TwinColSelectElement extends AbstractSelectElement {

    private Select options;
    private Select selectedOptions;
    private WebElement deselButton;
    private WebElement selButton;
    private static org.openqa.selenium.By bySelect = By.tagName("select");
    private static org.openqa.selenium.By byButton = By.className("v-button");
    private WebElement optionsElement;
    private WebElement selectionsElement;

    @Override
    protected void init() {
        super.init();
        List<WebElement> selectElements = findElements(bySelect);

        optionsElement = selectElements.get(0);
        selectionsElement = selectElements.get(1);

        options = new Select(optionsElement);
        selectedOptions = new Select(selectionsElement);
        List<WebElement> buttons = findElements(byButton);
        selButton = buttons.get(0);
        deselButton = buttons.get(1);
    }

    private void deselectAll() {
        if (selectedOptions.isMultiple()) {
            if (selectedOptions.getAllSelectedOptions()
                    .size() != selectedOptions.getOptions().size()) {
                for (int i = 0, l = selectedOptions.getOptions()
                        .size(); i < l; ++i) {
                    selectedOptions.selectByIndex(i);
                }
            }
            deselButton.click();
        }
        while (!selectedOptions.getOptions().isEmpty()) {
            selectedOptions.selectByIndex(0);
            deselButton.click();
        }
    }

    /**
     * Deselects the option with the given option text, i.e. removes it from the
     * right side column.
     *
     * @param text
     *            the text of the option to deselect
     */
    public void deselectByText(String text) {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        selectedOptions.deselectAll();
        selectedOptions.selectByVisibleText(text);
        deselButton.click();
    }

    /**
     * Functionality to find option texts of all currently selected options.
     *
     * @return List of visible text for all selected options
     */
    public List<String> getValues() {
        return getOptionsFromSelect(selectedOptions);
    }

    /**
     * Functionality to find all option texts.
     *
     * @return List of visible text for all options
     */
    public List<String> getOptions() {
        List<String> optionTexts = getOptionsFromSelect(options);
        optionTexts.addAll(getValues());
        return optionTexts;
    }

    /**
     * Gets the available option texts, i.e. all values which have not been
     * selected.
     *
     * @return List of visible text for available options
     */
    public List<String> getAvailableOptions() {
        return getOptionsFromSelect(options);
    }

    /**
     * Selects the option with the given option text, i.e. adds it to the right
     * side column.
     *
     * @param text
     *            the text of the option to select
     */
    public void selectByText(String text) {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }

        options.deselectAll();
        options.selectByVisibleText(text);
        selButton.click();
    }

    private List<String> getOptionsFromSelect(Select select) {
        List<String> optionTexts = new ArrayList<String>();
        for (WebElement option : select.getOptions()) {
            optionTexts.add(option.getText());
        }
        return optionTexts;
    }

    /**
     * Return first selected item (item in the right part of component).
     *
     * @return the option text for the item
     */
    public String getValue() {
        String value = "";
        WebElement selectedElement = findElement(
                By.className("v-select-twincol-selections"));
        List<WebElement> optionElements = selectedElement
                .findElements(By.tagName("option"));
        if (!optionElements.isEmpty()) {
            value = optionElements.get(0).getText();
        }
        return value;
    }

    @Override
    public void clear() {
        deselectAll();
    }

    /**
     * Gets the left {@code <select>} element inside the component, containing
     * the available options.
     *
     * @return the select element containing options inside the component
     * @since 8.1.1
     */
    public WebElement getOptionsElement() {
        return optionsElement;
    }

    /**
     * Gets the right {@code <select>} element inside the component, containing
     * the selected options.
     *
     * @return the select element containing selection inside the component
     * @since 8.1.1
     */
    public WebElement getSelectionsElement() {
        return selectionsElement;
    }
}
