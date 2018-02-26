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
     * @since
     */
    public String getValue() {
        JavascriptExecutor executor= (JavascriptExecutor)getDriver();
        return executor.executeScript("" +
                "var richTextArea = document.getElementsByClassName(\"v-richtextarea\");\n" +
                "var body = richTextArea[0].querySelector(\"iframe\").contentDocument.body;\n" +
                "return body.innerHTML;").toString();
    }

    /**
     * Set value of the field element.
     *
     * @param chars
     *            new value of the field
     *@since
     */
    public void setValue(CharSequence chars) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        clearElementClientSide();
        focus();
        JavascriptExecutor executor= (JavascriptExecutor)getDriver();
        executor.executeScript("" +
                "var richTextArea = document.getElementsByClassName(\"v-richtextarea\");\n" +
                "var body = richTextArea[0].querySelector(\"iframe\").contentDocument.body;\n" +
                "body.innerHTML=arguments[0]; \n" +
                "var ev = document.createEvent('HTMLEvents');\n" +
                "ev.initEvent('change', true, false); \n" +
                "body.dispatchEvent(ev);", chars);
    }

    private void clearElementClientSide(){
        waitForVaadin();
        JavascriptExecutor executor= (JavascriptExecutor)getDriver();
        executor.executeScript("" +
                "var richTextArea = document.getElementsByClassName(\"v-richtextarea\");\n" +
                "var body = richTextArea[0].querySelector(\"iframe\").contentDocument.body;\n" +
                "body.innerHTML=\"\"; \n");
    }

    @Override
    public void focus(){
        waitForVaadin();
        JavascriptExecutor executor= (JavascriptExecutor)getDriver();
        executor.executeScript("" +
                "var richTextArea = document.getElementsByClassName(\"v-richtextarea\");\n" +
                "var body = richTextArea[0].querySelector(\"iframe\").contentDocument.body;\n" +
                "body.focus();");
    }
}
