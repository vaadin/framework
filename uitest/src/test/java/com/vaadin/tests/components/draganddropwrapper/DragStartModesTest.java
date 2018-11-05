package com.vaadin.tests.components.draganddropwrapper;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class DragStartModesTest extends MultiBrowserTest {

    @Test
    public void testDragStartModes() throws IOException {
        openTestURL();
        WebElement dropTarget = vaadinElement(
                "/VVerticalLayout[0]/VVerticalLayout[0]/VLabel[0]");
        dragToTarget("COMPONENT", dropTarget);
        dragToTarget("WRAPPER", dropTarget);
        dragToTarget("COMPONENT_OTHER", dropTarget);
    }

    private void dragToTarget(String dragMode, WebElement dropTarget)
            throws IOException {
        WebElement draggable = vaadinElementById("label" + dragMode);
        new Actions(driver)
                .moveToElement(draggable, getXOffset(draggable, 10),
                        getYOffset(draggable, 10))
                .clickAndHold().moveByOffset(5, 0).perform();
        new Actions(driver).moveToElement(dropTarget,
                getXOffset(dropTarget, 12), getYOffset(dropTarget, 10))
                .perform();
        compareScreen("dragImageMode" + dragMode);
        new Actions(driver).release().perform();
    }

}
