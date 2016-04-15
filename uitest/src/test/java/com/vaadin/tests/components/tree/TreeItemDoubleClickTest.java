/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.components.tree;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeItemDoubleClickTest extends MultiBrowserTest {

    @Test
    public void test() throws InterruptedException {
        openTestURL();
        String caption = "Tree Item 2";
        doubleClick(getTreeNodeByCaption(caption));
        assertLogText("Double Click " + caption);

        changeImmediate();

        caption = "Tree Item 3";
        doubleClick(getTreeNodeByCaption(caption));
        assertLogText("Double Click " + caption);
    }

    private void changeImmediate() {
        $(ButtonElement.class).caption("Change immediate flag").first().click();
        assertLogText("tree.isImmediate() is now");
    }

    private WebElement getTreeNodeByCaption(String caption) {
        return getDriver().findElement(
                By.xpath("//span[text() = '" + caption + "']"));
    }

    private void doubleClick(WebElement element) {
        new Actions(getDriver()).doubleClick(element).build().perform();

    }

    private void assertLogText(String text) {
        assertThat(
                String.format("Couldn't find text '%s' from the log.", text),
                logContainsText(text));
    }

}
