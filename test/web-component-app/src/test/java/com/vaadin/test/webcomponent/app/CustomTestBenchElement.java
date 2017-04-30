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
package com.vaadin.test.webcomponent.app;

import com.vaadin.testbench.elementsbase.AbstractElement;

public class CustomTestBenchElement extends AbstractElement {
    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name
     *            the name of the property
     * @param value
     *            the value to set
     */
    public void setProperty(String name, String value) {
        getCommandExecutor().executeScript("arguments[0].value=arguments[1]",
                this, value);
    }

    /**
     * Gets a JavaScript property of the given element as a string.
     *
     * @param name
     *            the name of the property
     */
    public String getPropertyString(String name) {
        return String.valueOf(getProperty(name));
    }

    public Object getProperty(String name) {
        return getCommandExecutor()
                .executeScript("return arguments[0][arguments[1]]", this, name);
    }

    /**
     * Gets a JavaScript property of the given element as a double.
     *
     * @param name
     *            the name of the property
     */
    public Double getPropertyDouble(String name) {
        Object value = getProperty(name);

        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new IllegalArgumentException(
                    "Type of property is " + value.getClass().getSimpleName());
        }
    }
}
