package com.vaadin.tests.dd;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.testbench.elements.TreeElement;

public class DnDOnSubtreeTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return DDTest8.class;
    }

    @Test
    public void testDragAndDropOnSubTrees() throws Exception {
        openTestURL();
        TreeElement tree = $(TreeElement.class).first();
        WebElement bar2 = tree.findElement(By.vaadin("#n[3]"));
        WebElement bar5 = tree.findElement(By.vaadin("#n[6]"));
        new Actions(driver)
                .moveToElement(bar2, getXOffset(bar2, 11), getYOffset(bar2, 8))
                .clickAndHold().moveByOffset(10, 10).perform();
        /* Drop on Bar5, which is a subtree target */
        new Actions(driver)
                .moveToElement(bar5, getXOffset(bar5, 34), getYOffset(bar5, 9))
                .release().perform();
        WebElement target = tree.findElement(By.vaadin("#n[5]/expand"));
        testBenchElement(target).click(getXOffset(target, 5),
                getYOffset(target, 5));
        /* Assert that the dragged & dropped node is now a child of Bar5 */
        waitUntilElementPresent(tree, "#n[5]/n[0]");
    }

    private void waitUntilElementPresent(final TestBenchElement parent,
            final String vaadinSelector) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver arg0) {
                return parent.isElementPresent(By.vaadin(vaadinSelector));
            }

            @Override
            public String toString() {
                return String.format("element to contain '%s'", vaadinSelector);
            }
        });
    }
}
