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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.RichTextArea")
public class RichTextAreaElement extends AbstractFieldElement {

    /**
     * Gets the {@code <iframe>} element inside the component, containing the
     * editor.
     *
     * @return the iframe element containing the editor
     * @since 8.1.1
     */
    public WebElement getEditorIframe() {
        return findElement(By.tagName("iframe"));
    }

    /**
     * Return value of the field element.
     *
     * @return value of the field element
     * @since 8.4
     */
    public String getValue() {
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        return executor.executeScript(
                "return arguments[0].contentDocument.body.innerHTML",
                getEditorIframe()).toString();
    }

    /**
     * Set value of the field element.
     *
     * @param chars
     *            new value of the field
     * @since 8.4
     */
    public void setValue(CharSequence chars) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("var bodyE=arguments[0].contentDocument.body;\n"
                + "bodyE.innerHTML=arguments[1]; \n"
                + "var ev = document.createEvent('HTMLEvents');\n"
                + "ev.initEvent('change', true, false); \n"
                + "bodyE.dispatchEvent(ev);", getEditorIframe(), chars);
    }

    @Override
    public void focus() {
        waitForVaadin();
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("arguments[0].contentDocument.body.focus();",
                getEditorIframe());
    }
}
