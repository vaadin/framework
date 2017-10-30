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

import java.util.List;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.AbstractSplitPanel")
public class AbstractSplitPanelElement
        extends AbstractComponentContainerElement {

    private static org.openqa.selenium.By byFirstContainer = By
            .xpath("./div/div[contains(normalize-space(concat(@class, ' ')), "
                    + "normalize-space('-first-container '))]/*");
    private static org.openqa.selenium.By bySecondContainer = By
            .xpath("./div/div[contains(normalize-space(concat(@class, ' ')), "
                    + "normalize-space('-second-container '))]/*");

    /**
     * Gets the first component of a split panel and wraps it in given class.
     * 
     * @param clazz
     *            Components element class
     * @return First component wrapped in given class
     */
    public <T extends AbstractElement> T getFirstComponent(Class<T> clazz) {
        return getContainedComponent(clazz, byFirstContainer);
    }

    /**
     * Gets the second component of a split panel and wraps it in given class.
     * 
     * @param clazz
     *            Components element class
     * @return Second component wrapped in given class
     */
    public <T extends AbstractElement> T getSecondComponent(Class<T> clazz) {
        return getContainedComponent(clazz, bySecondContainer);
    }

    /**
     * Gets a component of a split panel and wraps it in the given class.
     * 
     * @param clazz
     *            Components element class
     * @param byContainer
     *            A locator that specifies the container (first or second) whose
     *            component is looked for
     * @return A component wrapped in the given class
     */
    private <T extends AbstractElement> T getContainedComponent(Class<T> clazz,
            org.openqa.selenium.By byContainer) {
        List<AbstractComponentElement> containedComponents = $$(
                AbstractComponentElement.class).all();
        List<WebElement> componentsInSelectedContainer = findElements(
                byContainer);
        for (AbstractComponentElement component : containedComponents) {
            WebElement elem = component.getWrappedElement();
            if (componentsInSelectedContainer.contains(elem)) {
                return TestBench.createElement(clazz, elem,
                        getCommandExecutor());
            }
        }
        return null;
    }

}
