package com.vaadin.tests.components.combobox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ComboBoxTestBenchPerformanceTest extends SingleBrowserTest {

    /**
     * TestBench timeout is 20s, require 15s to make sure cluster load won't
     * affect the result badly.
     */
    private static final double TIME_LIMIT = 15000d;

    @Test
    public void testSelectionPerformance() throws Exception {
        openTestURL();

        long before = System.currentTimeMillis();
        setComboBoxValue("abc123"); // new
        long after = System.currentTimeMillis();
        assertThat((double) after - before, closeTo(0d, TIME_LIMIT));

        before = System.currentTimeMillis();
        setComboBoxValue("11"); // existing (2nd page)
        after = System.currentTimeMillis();
        assertThat((double) after - before, closeTo(0d, TIME_LIMIT));

        before = System.currentTimeMillis();
        setComboBoxValue("abc123"); // previously added (3rd page)
        after = System.currentTimeMillis();
        assertThat((double) after - before, closeTo(0d, TIME_LIMIT));
    }

    public void setComboBoxValue(final String value) {
        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        if (combobox.getPopupSuggestions().contains(value)) {
            // Select existing item
            combobox.selectByText(value);
        } else {
            // Enter new item
            combobox.clear();
            combobox.sendKeys(value);
            combobox.sendKeys(Keys.ENTER);
        }
    }
}
