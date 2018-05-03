package com.vaadin.tests.components.splitpanel;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for horizontal split panel height in case when only second component is
 * set.
 *
 * @author Vaadin Ltd
 */
public class HorizontalSplitPanelHeightTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    @Test
    public void testHorizontalWithoutFirstComponent() {
        testSplitPanel("Horizontal 1");
    }

    @Test
    public void testHorizontalWithFirstComponent() {
        testSplitPanel("Horizontal 2");
    }

    @Test
    public void testHorizontalWithFixedHeight() {
        testSplitPanel("Horizontal 3");
    }

    @Test
    public void testVerticalWithoutFirstComponent() {
        testSplitPanel("Vertical 1");
    }

    private void testSplitPanel(String id) {
        WebElement splitPanel = findElement(By.id(id));
        WebElement label = splitPanel.findElement(By.className("target"));
        Assert.assertTrue(
                id + ": split panel height (" + splitPanel.getSize().getHeight()
                        + ") is less than " + "height of second component ("
                        + label.getSize().getHeight() + ")",
                splitPanel.getSize().getHeight() >= label.getSize()
                        .getHeight());
        Assert.assertEquals("Label text in the second panel is not visible",
                "Label", label.getText());
    }
}
