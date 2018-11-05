package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests Table Header ClickListener
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
