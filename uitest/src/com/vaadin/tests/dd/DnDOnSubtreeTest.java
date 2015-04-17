package com.vaadin.tests.dd;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TreeElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

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
        new Actions(driver).moveToElement(bar2, 11, 8).clickAndHold()
                .moveByOffset(10, 10).perform();
        /* Drop on Bar5, which is a subtree target */
        new Actions(driver).moveToElement(bar5, 34, 9).release().perform();
        testBenchElement(tree.findElement(By.vaadin("#n[5]/expand"))).click(5,
                5);
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
