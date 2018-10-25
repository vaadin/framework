package com.vaadin.tests.components.datefield;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

public class InlineDateFieldReadOnlyTest extends MultiBrowserTest {

    @Test
    public void minutesAndSecondsAreRenderedCorrectly() {
        openTestURL();
        WebElement divTime = findElement(By.className("v-datefield-time"));
        List<WebElement> labels = divTime.findElements(By.className("v-label"));
        assertEquals(5, labels.size());

        // At positions 1 and 3 the delimeter label is set
        assertEquals(InlineDateFieldReadOnly.HOUR,
                convertToInt(labels.get(0).getText()));
        assertEquals(InlineDateFieldReadOnly.MIN,
                convertToInt(labels.get(2).getText()));
        assertEquals(InlineDateFieldReadOnly.SEC,
                convertToInt(labels.get(4).getText()));
    }

    private int convertToInt(String value) {
        int converted = -1;
        try {
            converted = Integer.valueOf(value).intValue();
        } catch (NumberFormatException e) {
            assertFalse(
                    "NumberFormatException is thrown! Conversion was not possible",
                    e instanceof NumberFormatException);
        }
        return converted;
    }
}
