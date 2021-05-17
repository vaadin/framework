package com.vaadin.tests.layouts.layouttester.VLayout;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.layouts.layouttester.BaseAlignmentTest;

public class VAlignmentTest extends BaseAlignmentTest {

    @Override
    public void layoutAlignment() throws IOException {
        super.layoutAlignment();

        // The layout is too high to fit into one screenshot, we need to scroll
        // and take another.

        List<TextFieldElement> textFields = $(TextFieldElement.class).all();
        assertEquals(9, textFields.size());
        TextFieldElement lastTextField = textFields.get(8);

        // moveToElement fails on Firefox since the component is out of viewport
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", lastTextField);

        compareScreen("alignment-scrolled");
    }
}
