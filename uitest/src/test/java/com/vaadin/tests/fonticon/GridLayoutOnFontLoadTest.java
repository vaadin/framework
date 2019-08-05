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

    private ExpectedCondition<Boolean> checkNoOverlapping(String element1,
            int position1, String element2, int position2) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver arg0) {
                return position1 <= position2;
            }

            @Override
            public String toString() {
                // waiting for ...
                return String
                        .format("the coordinates of the inspected elements ("
                                + element1 + ", " + element2
                                + ") to not overlap");
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

        waitUntil(checkNoOverlapping("button",
                button.getLocation().getX() + button.getSize().width,
                "checkbox", checkbox.getLocation().getX()));
        waitUntil(checkNoOverlapping("textarea",
                textarea.getLocation().getY() + textarea.getSize().height + 10,
                "grid", grid.getLocation().getY()));
    }

}
