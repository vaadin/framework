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

import com.vaadin.testbench.elementsbase.ServerClass;

/**
 * Element class for testing AbstractDateField.
 */
@ServerClass("com.vaadin.ui.AbstractDateField")
public class AbstractDateFieldElement extends AbstractFieldElement {

    /**
     * Gets the value of the date field as a ISO8601 compatible string
     * (yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss depending on whether the element
     * supports time).
     *
     * @return the date in ISO-8601 format
     * @since 8.1.0
     */
    protected String getISOValue() {
        return (String) getCommandExecutor()
                .executeScript("return arguments[0].getISOValue();", this);
    }

    /**
     * Sets the value of the date field as a ISO8601 compatible string
     * (yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss depending on whether the element
     * supports time).
     *
     * @param isoDateValue
     *            the date in ISO-8601 format
     * @since 8.1.0
     */
    protected void setISOValue(String isoDateValue) {
        getCommandExecutor().executeScript(
                "arguments[0].setISOValue(arguments[1]);", this, isoDateValue);
    }

}
