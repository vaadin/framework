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
package com.vaadin.tests.components.tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SelectItemAfterRemoveTest extends MultiBrowserTest {

    @Test
    public void selectedItemIsSelected() {
        openTestURL();

        getSecondSpan().click();

        assertThat(getNodes().size(), is(2));
        assertThat(getFirstNode().getAttribute("class"),
                containsString("v-tree-node-selected"));
    }

    private WebElement getFirstNode() {
        return getNodes().get(0);
    }

    private List<WebElement> getNodes() {
        return findElements(By.className("v-tree-node-caption"));
    }

    private WebElement getSecondSpan() {
        for (WebElement e : getSpans()) {
            if (e.getText().equals("second")) {
                return e;
            }
        }

        return null;
    }

    private List<WebElement> getSpans() {
        return findElements(By.tagName("span"));
    }
}
