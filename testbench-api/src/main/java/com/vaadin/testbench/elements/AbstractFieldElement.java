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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.AbstractField")
public class AbstractFieldElement extends AbstractComponentElement {

    /**
     * Select contents of TextField Element
     *
     * NOTE: When testing with firefox browser window should have focus in it
     *
     * @since 8.0
     * @param elem
     *            element which context will be select
     */
    protected void clientSelectElement(WebElement elem) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        String script = "window.focus();" + "var elem=arguments[0];"
                + "elem.select();elem.focus();";
        js.executeScript(script, elem);
    }

    protected void clearElementClientSide(WebElement elem) {
        // clears without triggering an event (on client side)
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        String script = "window.focus(); var elem=arguments[0];"
                + "elem.value=\"\";";
        js.executeScript(script, elem);
    }

}
