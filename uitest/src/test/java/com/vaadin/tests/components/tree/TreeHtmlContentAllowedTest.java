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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TreeElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeHtmlContentAllowedTest extends SingleBrowserTest {

    @Test
    public void testTreeHtmlContentAllowed() {
        openTestURL();

        CheckBoxElement toggle = $(CheckBoxElement.class).first();
        Assert.assertEquals("HTML content should be disabled by default",
                "unchecked", toggle.getValue());

        // Markup is seen as plain text
        assertTreeCaptionTexts("Just text", "Some <b>html</b>",
                "Child <span id='my-html-element'>element html</span>");

        toggle.click();
        assertTreeCaptionTexts("Just text", "Some html", "Child element html");

        // Expand the HTML parent
        findElements(By.className("v-tree-node")).get(1).click();

        assertTreeCaptionTexts("Just text", "Some html", "Child html",
                "Child element html");

        toggle.click();
        assertTreeCaptionTexts("Just text", "Some <b>html</b>",
                "Child <i>html</i>",
                "Child <span id='my-html-element'>element html</span>");

        toggle.click();
        findElements(By.id("my-html-element")).get(0).click();
        assertHtmlElementSelected();

    }

    private void assertHtmlElementSelected() {
        TreeElement tree = $(TreeElement.class).first();
        Assert.assertEquals(tree.getValue(), "Child element html");
    }

    private void assertTreeCaptionTexts(String... captions) {
        TreeElement tree = $(TreeElement.class).first();
        List<WebElement> nodes = tree
                .findElements(By.className("v-tree-node-caption"));

        Assert.assertEquals(captions.length, nodes.size());
        for (int i = 0; i < captions.length; i++) {
            Assert.assertEquals(captions[i], nodes.get(i).getText());
        }
    }

}
