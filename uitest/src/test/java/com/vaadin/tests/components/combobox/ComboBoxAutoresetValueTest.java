package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ComboBoxAutoresetValueTest extends SingleBrowserTest {

    @Test
    public void testValueChanges() {
        openTestURL();

        ComboBoxElement comboBox = $(ComboBoxElement.class).first();

        assertEquals("", comboBox.getValue());

        comboBox.selectByText(ComboBoxAutoresetValue.RESET);

        assertLogChange(1, ComboBoxAutoresetValue.RESET, 1);
        assertLogChange(2, null, 0);
        assertEquals("", comboBox.getValue());

        comboBox.selectByText(ComboBoxAutoresetValue.CHANGE);
        assertLogChange(3, ComboBoxAutoresetValue.CHANGE, 1);
        assertLogChange(4, ComboBoxAutoresetValue.SOMETHING, 0);
        assertEquals(ComboBoxAutoresetValue.SOMETHING, comboBox.getValue());

        comboBox.selectByText(ComboBoxAutoresetValue.SOMETHING);
        // No new log items
        assertLogChange(4, ComboBoxAutoresetValue.SOMETHING, 0);
        assertEquals(ComboBoxAutoresetValue.SOMETHING, comboBox.getValue());
    }

    private void assertLogChange(int sequenceNumber, String expectedValue,
            int rowIndex) {
        assertEquals(sequenceNumber + ". Value changed to " + expectedValue,
                getLogRow(rowIndex));
    }
}
