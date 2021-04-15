package com.vaadin.tests.layouts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ProgressBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UpdateComponentWithinExpandRatioTest extends MultiBrowserTest {

    @Test
    public void updateProgressShouldNotMoveButton() {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();
        ProgressBarElement pb = $(ProgressBarElement.class).first();
        ButtonElement button = $(ButtonElement.class).first();

        int initialX = button.getLocation().getX();
        int initialWidth = pb.getSize().getWidth();

        button.click();
        waitUntilLoadingIndicatorNotVisible();

        assertEquals("Button's position changed unexpectedly", initialX,
                button.getLocation().getX());
        assertEquals("ProgressBar's width changed unexpectedly", initialWidth,
                pb.getSize().getWidth());
    }
}
