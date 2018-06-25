package com.vaadin.tests.components.formlayout;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for H2 label inside FormLayout as first child of VerticalLayout.
 *
 * @author Vaadin Ltd
 */
public class FormLayoutInVerticalLayoutTest extends MultiBrowserTest {

    @Test
    public void testHeaderMarginInFormLayout() {
        openTestURL();

        List<WebElement> labels = findElements(By.className("v-label-h2"));
        String formLabelMargin = labels.get(0).getCssValue("margin-top");
        String verticalLayoutLabelMargin = labels.get(1)
                .getCssValue("margin-top");
        Assert.assertNotEquals(
                "'margin-top' values for header label in vertical layout "
                        + "and form layout are the same",
                verticalLayoutLabelMargin, formLabelMargin);
    }
}
