package com.vaadin.tests.fonticon;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutOnFontLoadTest extends MultiBrowserTest {

    ButtonElement button;
    CheckBoxElement checkbox;
    TextAreaElement textarea;
    GridElement grid;

    private ExpectedCondition<Boolean> expectedCondition(int element1, int element2) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver arg0) {
                return element1 <= element2;
            }

            @Override
            public String toString() {
                // waiting for...
                return String.format(
                        "There should be no overlaps between two elements");
            }
        };
    }

    @Test
    public void testComponentsDontOverlap() throws Exception {
        openTestURL();
        // Make sure fonts are loaded.
        sleep(1000);

        button = $(ButtonElement.class).first();
        checkbox = $(CheckBoxElement.class).first();
        textarea = $(TextAreaElement.class).first();
        grid = $(GridElement.class).first();

        waitUntil(expectedCondition(button.getLocation().getX() + button.getSize(). width,checkbox
                .getLocation().getX()));
        waitUntil(expectedCondition(textarea.getLocation().getY() + textarea.getSize().height
                + 10, grid.getLocation().getY()));
    }

}
