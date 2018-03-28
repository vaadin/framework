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
 * @since
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
