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
public class HeaderClickTest extends MultiBrowserTest {

    @Test
    public void testFooter() throws IOException {
        openTestURL();

        TableElement table = $(TableElement.class).first();
        TestBenchElement header0 = table.getHeaderCell(0);
        header0.click();

        TextFieldElement tf = $(TextFieldElement.class).first();
        assertEquals("col1", tf.getValue());

        table = $(TableElement.class).first();
        TestBenchElement header1 = table.getHeaderCell(1);
        header1.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col2", tf.getValue());

        table = $(TableElement.class).first();
        TestBenchElement header2 = table.getHeaderCell(2);
        header2.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col3", tf.getValue());

        CheckBoxElement cb = $(CheckBoxElement.class).first();
        cb.click();

        table = $(TableElement.class).first();
        header0 = table.getHeaderCell(0);
        header0.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col1", tf.getValue());

        table = $(TableElement.class).first();
        header1 = table.getHeaderCell(1);
        header1.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col2", tf.getValue());

        table = $(TableElement.class).first();
        header2 = table.getHeaderCell(2);
        header2.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col3", tf.getValue());

        cb = $(CheckBoxElement.class).get(1);
        cb.click();

        table = $(TableElement.class).first();
        header0 = table.getHeaderCell(0);
        header0.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col1", tf.getValue());

        table = $(TableElement.class).first();
        header1 = table.getHeaderCell(1);
        header1.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col2", tf.getValue());

        table = $(TableElement.class).first();
        header2 = table.getHeaderCell(2);
        header2.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col3", tf.getValue());

        cb = $(CheckBoxElement.class).get(2);
        cb.click();

        table = $(TableElement.class).first();
        header0 = table.getHeaderCell(0);
        header0.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col1", tf.getValue());

        table = $(TableElement.class).first();
        header1 = table.getHeaderCell(1);
        header1.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col2", tf.getValue());

        table = $(TableElement.class).first();
        header2 = table.getHeaderCell(2);
        header2.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col3", tf.getValue());

        cb = $(CheckBoxElement.class).get(2);
        cb.click();

        tf = $(TextFieldElement.class).first();
        assertEquals("col3", tf.getValue());

    }
}
