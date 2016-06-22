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
package com.vaadin.tests.components.tabsheet;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * If user selected the last tab the test will change it back to the first one
 * from a server side selection listener. This test makes sure that actually
 * happen.
 *
 * @author Vaadin Ltd
 */
public class TabSelectionRevertedByServerTest extends MultiBrowserTest {

    @Test
    public void testFocus() throws InterruptedException, IOException {
        openTestURL();

        // Selects Tab 4 which should be selected.
        click(4);
        assertSelection(4, 1);

        // Select Tab 5 which should revert to Tab 1.
        click(5);
        assertSelection(1, 5);

        // Make sure after reverting the selection the tab selection still
        // works.
        click(3);
        assertSelection(3, 1);

    }

    private void assertSelection(int expectedIndex, int wrongIndex) {
        TestBenchElement tabExpected = tab(expectedIndex);
        String attributeClassExpected = tabExpected.getAttribute("class");

        Assert.assertTrue("Tab " + expectedIndex + " should be selected.",
                attributeClassExpected
                        .contains("v-tabsheet-tabitemcell-selected"));

        TestBenchElement tabWrong = tab(wrongIndex);
        String attributeClassWrong = tabWrong.getAttribute("class");

        Assert.assertTrue("Tab " + wrongIndex
                + " should be selected when click on Tab 4",
                !attributeClassWrong
                        .contains("v-tabsheet-tabitemcell-selected"));
    }

    /*
     * Click on the element.
     */
    private void click(int tabIndex) throws InterruptedException {
        click(tab(tabIndex));
    }

    /*
     * Click on the element.
     */
    private void click(TestBenchElement element) throws InterruptedException {

        element.click(10, 10);
        if (DELAY > 0) {
            sleep(DELAY);
        }
    }

    /*
     * Delay for PhantomJS.
     */
    private final static int DELAY = 10;

    /*
     * Provide the tab at specified index.
     */
    private TestBenchElement tab(int index) {
        By by = By.className("v-tabsheet-tabitemcell");

        TestBenchElement element = (TestBenchElement) getDriver().findElements(
                by).get(index - 1);

        String expected = "Tab " + index;
        Assert.assertEquals(expected,
                element.getText().substring(0, expected.length()));

        return element;
    }

}
