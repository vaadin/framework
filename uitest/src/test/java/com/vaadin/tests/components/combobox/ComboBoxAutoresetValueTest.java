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
package com.vaadin.tests.components.combobox;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ComboBoxAutoresetValueTest extends SingleBrowserTest {

    @Test
    public void testValueChanges() {
        openTestURL();

        ComboBoxElement comboBox = $(ComboBoxElement.class).first();

        Assert.assertEquals("", comboBox.getValue());

        comboBox.selectByText(ComboBoxAutoresetValue.RESET);

        assertLogChange(1, ComboBoxAutoresetValue.RESET, 1);
        assertLogChange(2, null, 0);
        Assert.assertEquals("", comboBox.getValue());

        comboBox.selectByText(ComboBoxAutoresetValue.CHANGE);
        assertLogChange(3, ComboBoxAutoresetValue.CHANGE, 1);
        assertLogChange(4, ComboBoxAutoresetValue.SOMETHING, 0);
        Assert.assertEquals(ComboBoxAutoresetValue.SOMETHING,
                comboBox.getValue());

        comboBox.selectByText(ComboBoxAutoresetValue.SOMETHING);
        // No new log items
        assertLogChange(4, ComboBoxAutoresetValue.SOMETHING, 0);
        Assert.assertEquals(ComboBoxAutoresetValue.SOMETHING,
                comboBox.getValue());
    }

    private void assertLogChange(int sequenceNumber, String expectedValue,
            int rowIndex) {
        Assert.assertEquals(
                sequenceNumber + ". Value changed to " + expectedValue,
                getLogRow(rowIndex));
    }
}
