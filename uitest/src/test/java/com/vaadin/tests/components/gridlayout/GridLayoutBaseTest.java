package com.vaadin.tests.components.gridlayout;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class GridLayoutBaseTest extends MultiBrowserTest {
    @Test
    public void cellSizesAreCorrectlyCalculated() {
        openTestURL();

        hideMiddleRowAndColumn();
        final List<WebElement> slots4x4 = getSlots(1);

        waitUntilColumnAndRowAreHidden(slots4x4);
        final List<WebElement> slots5x5 = getSlots(0);

        for (int i = 0; i < slots5x5.size(); i++) {
            WebElement compared = slots5x5.get(i);
            WebElement actual = slots4x4.get(i);
            assertEquals("Different top coordinate for element " + i,
                    compared.getCssValue("top"), actual.getCssValue("top"));
            assertEquals("Different left coordinate for element " + i,
                    compared.getCssValue("left"), actual.getCssValue("left"));
        }
    }

    private void waitUntilColumnAndRowAreHidden(
            final List<WebElement> slots4x4) {
        waitUntil(input -> getSlots(0).size() == slots4x4.size(), 5);
    }

    private List<WebElement> getSlots(int index) {
        GridLayoutElement layout = $(GridLayoutElement.class).get(index);

        return layout.findElements(By.className("v-gridlayout-slot"));
    }

    private void hideMiddleRowAndColumn() {
        $(ButtonElement.class).first().click();
    }
}
