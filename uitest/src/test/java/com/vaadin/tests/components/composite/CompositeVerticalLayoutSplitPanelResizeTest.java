package com.vaadin.tests.components.composite;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.HorizontalSplitPanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CompositeVerticalLayoutSplitPanelResizeTest
        extends MultiBrowserTest {

    @Test
    public void testResize() {
        getDriver().manage().window().setSize(new Dimension(600, 400));
        openTestURL();

        WebElement root = findElement(By.id("root"));
        Dimension oldRootSize = root.getSize();

        // inner level element that is expected to resize
        HorizontalSplitPanelElement content = $(
                HorizontalSplitPanelElement.class).first();
        Dimension oldContentSize = content.getSize();

        // resize
        getDriver().manage().window().setSize(new Dimension(500, 500));
        waitUntilLoadingIndicatorNotVisible();

        Dimension newRootSize = root.getSize();
        Dimension newContentSize = content.getSize();

        assertGreater("Unexpected vertical root size.", newRootSize.getHeight(),
                oldRootSize.getHeight());
        assertGreater("Unexpected horizontal root size.",
                oldRootSize.getWidth(), newRootSize.getWidth());

        assertGreater("Unexpected vertical content size.",
                newContentSize.getHeight(), oldContentSize.getHeight());
        assertGreater("Unexpected horizontal content size.",
                oldContentSize.getWidth(), newContentSize.getWidth());
    }
}
