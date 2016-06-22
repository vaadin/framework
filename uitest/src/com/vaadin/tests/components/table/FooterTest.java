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
package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests Table Footer
 * 
 * @author Vaadin Ltd
 */
public class FooterTest extends MultiBrowserTest {

    @Test
    public void testFooter() throws IOException {
        openTestURL();

        waitForElementPresent(By.className("v-table"));

        compareScreen("initial");

        TableElement table = $(TableElement.class).first();

        TestBenchElement footer1 = table.getFooterCell(0);
        TestBenchElement footer2 = table.getFooterCell(1);
        TestBenchElement footer3 = table.getFooterCell(2);

        assertEquals("Footer1", footer1.getText());
        assertEquals("Footer2", footer2.getText());
        assertEquals("Footer3", footer3.getText());

        CheckBoxElement checkBox = $(CheckBoxElement.class).first();
        checkBox.click();

        if (!BrowserUtil.isIE8(getDesiredCapabilities())) {
            // excluded IE8 since its screenshots varies from run-to-run
            compareScreen("no-footer");
        }

        checkBox.click();

        if (!BrowserUtil.isIE8(getDesiredCapabilities())) {
            // excluded IE8 since its screenshots varies from run-to-run
            compareScreen("footer-col1-col2-col3-a");
        }

        table = $(TableElement.class).first();

        footer1 = table.getFooterCell(0);
        footer2 = table.getFooterCell(1);
        footer3 = table.getFooterCell(2);

        assertEquals("Footer1", footer1.getText());
        assertEquals("Footer2", footer2.getText());
        assertEquals("Footer3", footer3.getText());

        // open table column selector menu
        table.findElement(By.className("v-table-column-selector")).click();
        // hide col2
        findElements(By.className("gwt-MenuItem")).get(1).click();

        if (!BrowserUtil.isIE8(getDesiredCapabilities())) {
            // excluded IE8 since its screenshots varies from run-to-run
            compareScreen("footer-col1-col3");
        }

        // open table column selector menu
        table.findElement(By.className("v-table-column-selector")).click();
        // show col2
        findElements(By.className("gwt-MenuItem")).get(1).click();

        if (!BrowserUtil.isIE8(getDesiredCapabilities())) {
            // excluded IE8 since its screenshots varies from run-to-run
            compareScreen("footer-col1-col2-col3-b");
        }

        TextFieldElement tf = $(TextFieldElement.class).first();
        tf.clear();
        waitUntiltextFieldIsChangedTo(tf, "");
        tf.sendKeys("fuu");
        waitUntiltextFieldIsChangedTo(tf, "fuu");
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        table = $(TableElement.class).first();
        footer1 = table.getFooterCell(0);
        assertEquals("fuu", footer1.getText());

        tf = $(TextFieldElement.class).get(1);
        tf.clear();
        waitUntiltextFieldIsChangedTo(tf, "");
        tf.sendKeys("bar");
        waitUntiltextFieldIsChangedTo(tf, "bar");
        button = $(ButtonElement.class).get(1);
        button.click();
        table = $(TableElement.class).first();
        footer2 = table.getFooterCell(1);
        assertEquals("bar", footer2.getText());

        tf = $(TextFieldElement.class).get(2);
        tf.clear();
        waitUntiltextFieldIsChangedTo(tf, "");
        button = $(ButtonElement.class).get(2);
        button.click();
        table = $(TableElement.class).first();
        footer3 = table.getFooterCell(2);
        assertEquals("", footer3.getText().trim());

        TextFieldElement tf1 = $(TextFieldElement.class).first();
        tf1.clear();
        waitUntiltextFieldIsChangedTo(tf1, "");
        tf1.sendKeys("Footer1");
        waitUntiltextFieldIsChangedTo(tf1, "Footer1");

        TextFieldElement tf2 = $(TextFieldElement.class).get(1);
        tf2.clear();
        waitUntiltextFieldIsChangedTo(tf2, "");
        tf2.sendKeys("Footer2");
        waitUntiltextFieldIsChangedTo(tf2, "Footer2");

        TextFieldElement tf3 = $(TextFieldElement.class).get(2);
        tf3.clear();
        waitUntiltextFieldIsChangedTo(tf3, "");
        tf3.sendKeys("Footer3");
        waitUntiltextFieldIsChangedTo(tf3, "Footer3");

        button = $(ButtonElement.class).first();
        button.click();
        button = $(ButtonElement.class).get(1);
        button.click();
        button = $(ButtonElement.class).get(2);
        button.click();

        waitUntilFooterCellIsChangedTo(0, "Footer1");
        waitUntilFooterCellIsChangedTo(1, "Footer2");
        waitUntilFooterCellIsChangedTo(2, "Footer3");

        table = $(TableElement.class).first();
        footer1 = table.getFooterCell(0);
        assertEquals("Footer1", footer1.getText());

        if (!BrowserUtil.isIE8(getDesiredCapabilities())) {
            // excluded IE8 since its screenshots varies from run-to-run
            compareScreen("footer-col1-col2-col3-c");
        }
    }

    private void waitUntiltextFieldIsChangedTo(final TextFieldElement tf,
            final String text) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return text.equals(tf.getValue());
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return String.format("textfields value was '%s'",
                        "" + tf.getText());
            }
        });
    }

    private void waitUntilFooterCellIsChangedTo(final int cell,
            final String footer) {
        final TestBenchElement footerCell = $(TableElement.class).first()
                .getFooterCell(cell);

        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return footer.equals(footerCell.getText());
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return String.format("footer cell %s's text was'%s'",
                        "" + cell, footerCell.getText());
            }
        });
    }
}
