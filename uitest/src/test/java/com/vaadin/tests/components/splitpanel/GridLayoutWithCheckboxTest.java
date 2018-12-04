package com.vaadin.tests.components.splitpanel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutWithCheckboxTest extends MultiBrowserTest {

    private TextFieldElement tf;
    private WebElement tfSlot;
    private CheckBoxElement cb;
    private WebElement cbSlot;
    private Dimension tfSize;
    private Dimension tfSlotSize;
    private Dimension cbSize;
    private Dimension cbSlotSize;

    @Test
    public void layoutShouldStayTheSame() {
        openTestURL();
        tf = $(TextFieldElement.class).first();
        tfSlot = tf.findElement(By.xpath(".."));
        cb = $(CheckBoxElement.class).first();
        cbSlot = cb.findElement(By.xpath(".."));

        // Doing anything with the textfield or checkbox should not affect
        // layout

        tf.setValue("a");
        assertSizes();
        cb.click();
        assertSizes();
        tf.setValue("b");
        assertSizes();
        cb.click();
        assertSizes();

    }

    private void assertSizes() {
        if (tfSize == null) {
            tfSize = tf.getSize();
            tfSlotSize = tfSlot.getSize();
            cbSize = cb.getSize();
            cbSlotSize = cbSlot.getSize();
        } else {
            assertEquals(tfSize, tf.getSize());
            assertEquals(tfSlotSize, tfSlot.getSize());
            assertEquals(cbSize, cb.getSize());
            assertEquals(cbSlotSize, cbSlot.getSize());
        }

    }
}
