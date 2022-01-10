/*
 * Copyright 2000-2022 Vaadin Ltd.
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.ServerClass;

/**
 * Element class for testing DateField.
 */
@ServerClass("com.vaadin.ui.DateField")
public class DateFieldElement extends AbstractDateFieldElement {

    /**
     * Clear DateField element.
     */
    @Override
    public void clear() {
        getInputElement().clear();
    }

    /**
     * Return value of the date field element.
     *
     * @return value of the date field element
     */
    public String getValue() {
        return getInputElement().getAttribute("value");
    }

    /**
     * Set value of the date field element.
     *
     * @param chars
     *            new value of the date field
     * @throws ReadOnlyException
     *             if the date field is in readonly mode
     */
    public void setValue(CharSequence chars) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        WebElement elem = getInputElement();
        TestBenchElement tbElement = (TestBenchElement) elem;
        clearElementClientSide(tbElement);
        tbElement.sendKeys(chars);
        tbElement.sendKeys(Keys.TAB);
    }

    /**
     * Opens the date field popup.
     */
    public void openPopup() {
        findElement(By.tagName("button")).click();
    }

    /**
     * Sets the value to the given date and time.
     *
     * @param value
     *            the date and time to set.
     */
    public void setDate(LocalDate value) {
        setISOValue(value.format(getISOFormatter()));
    }

    /**
     * Gets the value as a LocalDate object.
     *
     * @return the current value as a date object, or null if a date is not set
     *         or if the text field contains an invalid date
     */
    public LocalDate getDate() {
        String value = getISOValue();
        if (value == null) {
            return null;
        }
        return LocalDate.parse(value, getISOFormatter());
    }

    /**
     * Gets a date and time formatter for ISO-8601 dates.
     *
     * @return a date formatter for ISO-8601
     * @since 8.1.0
     */
    protected DateTimeFormatter getISOFormatter() {
        return DateTimeFormatter.ISO_LOCAL_DATE;
    }

    /**
     * Gets the {@code <input>} element inside the component.
     *
     * @return the input element inside the component
     * @since 8.1.1
     */
    public WebElement getInputElement() {
        return findElement(By.tagName("input"));
    }

}
