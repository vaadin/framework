package com.vaadin.v7.tests.components.tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeItemSelectionWithoutImmediateTest extends MultiBrowserTest {

    private static final long serialVersionUID = 1L;

    @Test
    public void testSelectTreeWithItemClickListenerNotImmediate()
            throws InterruptedException {
        openTestURL();

        // click on item i (in circle we select next item and check if it is
        // selected in tree)
        for (int i = 1; i <= 4; i++) {
            WebElement treeItem = getTreeNode(String.format(
                    TreeItemSelectionWithoutImmediate.MENU_ITEM_TEMPLATE, i));

            new Actions(getDriver()).moveToElement(treeItem).click().perform();
            Thread.sleep(100);

            WebElement selectedElement = driver
                    .findElement(By.className("v-tree-node-selected"));

            treeItem = getTreeNode(String.format(
                    TreeItemSelectionWithoutImmediate.MENU_ITEM_TEMPLATE, i));

            assertThat("Clicked element should be selected",
                    selectedElement.getText().equals(treeItem.getText()),
                    is(true));
        }
    }

    private WebElement getTreeNode(String caption) {
        return getDriver()
                .findElement(By.xpath("//span[text() = '" + caption + "']"));
    }
}
