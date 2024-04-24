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

import com.vaadin.testbench.elementsbase.ServerClass;

/**
 * A common base element class for all single select components.
 *
 * @since 8.1.1
 */
@ServerClass("com.vaadin.ui.AbstractSingleSelect")
public abstract class AbstractSingleSelectElement
        extends AbstractSelectElement {

    /**
     * Selects the first option in this single select component that matches the
     * given text.
     *
     * @param text
     *            the text to select by
     */
    public abstract void selectByText(String text);

    /**
     * Return value of this single select component.
     * <p>
     * <strong>Note:</strong> If there is no value selected the behavior of
     * subclasses varies. Pay attention on the actual implementation.
     *
     * @return the value
     */
    public abstract String getValue();
}
