package com.vaadin.tests.layouts.layouttester.GridLayout;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.layouts.layouttester.BaseLayoutRegErrorTest;

public class GridLayoutRegErrorTest extends BaseLayoutRegErrorTest {

    @Override
    public void LayoutRegError() throws IOException {
        super.LayoutRegError();

        // The layout is too high to fit into one screenshot, we need to scroll
        // and take another.

        List<CheckBoxElement> checkBoxes = $(CheckBoxElement.class).all();
        assertEquals(3, checkBoxes.size());
        CheckBoxElement lastCheckBox = checkBoxes.get(2);

        // moveToElement fails on Firefox since the component is out of viewport
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", lastCheckBox);

        compareScreen("RegError-Scrolled");
    }
}
