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
package com.vaadin.tests.contextclick;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.v7.testbench.elements.TreeElement;

public class TreeContextClickTest extends AbstractContextClickTest {

    @Test
    public void testContextClickOnItem() {
        openTestURL();

        addOrRemoveTypedListener();

        List<WebElement> nodes = $(TreeElement.class).first()
                .findElements(By.className("v-tree-node"));

        contextClick(nodes.get(1));

        assertEquals("1. ContextClickEvent: Bar", getLogRow(0));

        contextClick(nodes.get(0));

        assertEquals("2. ContextClickEvent: Foo", getLogRow(0));
    }

    @Test
    public void testContextClickOnSubItem() {
        openTestURL();

        addOrRemoveTypedListener();

        List<WebElement> nodes = $(TreeElement.class).first()
                .findElements(By.className("v-tree-node"));

        new Actions(getDriver()).moveToElement(nodes.get(1), 10, 10).click()
                .perform();

        nodes = $(TreeElement.class).first()
                .findElements(By.className("v-tree-node"));
        contextClick(nodes.get(2));

        assertEquals("1. ContextClickEvent: Baz", getLogRow(0));
    }

    @Test
    public void testContextClickOnEmptyArea() {
        openTestURL();

        addOrRemoveTypedListener();

        contextClick($(TreeElement.class).first(), 20, 100);

        assertEquals("1. ContextClickEvent: null", getLogRow(0));
    }
}
