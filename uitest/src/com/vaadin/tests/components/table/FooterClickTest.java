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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests Table Footer ClickListener
 * 
 * @author Vaadin Ltd
 */
public class FooterClickTest extends MultiBrowserTest {

    @Test
    public void testFooter() throws IOException {
        openTestURL();

        TableElement table = $(TableElement.class).first();
        TestBenchElement footer0 = table.getFooterCell(0);
        footer0.click();

        TextFieldElement tf = $(TextFieldElement.class).id("ClickedColumn");
        assertEquals("col1", tf.getValue());

        assertAnyLogText("1. Clicked on footer: col1");

        table = $(TableElement.class).first();
        TestBenchElement footer1 = table.getFooterCell(1);
        footer1.click();

        tf = $(TextFieldElement.class).id("ClickedColumn");
        assertEquals("col2", tf.getValue());

        assertAnyLogText("2. Clicked on footer: col2");

        table = $(TableElement.class).first();
        TestBenchElement footer2 = table.getFooterCell(2);
        footer2.click();

        tf = $(TextFieldElement.class).id("ClickedColumn");
        assertEquals("col3", tf.getValue());

        assertAnyLogText("3. Clicked on footer: col3");

        CheckBoxElement cb = $(CheckBoxElement.class).first();
        cb.click();

        table = $(TableElement.class).first();
        footer0 = table.getFooterCell(0);
        footer0.click();

        tf = $(TextFieldElement.class).id("ClickedColumn");
        assertEquals("col1", tf.getValue());

        assertAnyLogText("4. Clicked on footer: col1");

        table = $(TableElement.class).first();
        footer1 = table.getFooterCell(1);
        footer1.click();

        tf = $(TextFieldElement.class).id("ClickedColumn");
        assertEquals("col2", tf.getValue());

        assertAnyLogText("5. Clicked on footer: col2");

        table = $(TableElement.class).first();
        footer2 = table.getFooterCell(2);
        footer2.click();

        tf = $(TextFieldElement.class).id("ClickedColumn");
        assertEquals("col3", tf.getValue());

        assertAnyLogText("6. Clicked on footer: col3");
    }

    private void assertAnyLogText(String... texts) {
        assertThat(String.format(
                "Correct log text was not found, expected any of %s",
                Arrays.asList(texts)), logContainsAnyText(texts));
    }

    private boolean logContainsAnyText(String... texts) {
        for (String text : texts) {
            if (logContainsText(text)) {
                return true;
            }
        }
        return false;
    }
}
