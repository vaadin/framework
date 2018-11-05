package com.vaadin.v7.tests.components.tree;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 *
 * @since 7.1.9
 * @author Vaadin Ltd
 */
public class TreeScrollingOnRightClickTest extends MultiBrowserTest {

    @Test
    public void testScrollingOnRightClick() throws Throwable {
        openTestURL();

        // Focus tree
        WebElement tree = getDriver()
                .findElement(By.id(TreeScrollingOnRightClick.TREE_ID));
        tree.click();

        // Move selection down 50 items
        for (int down = 0; down < 50; down++) {
            tree.sendKeys(Keys.ARROW_DOWN);
        }

        Thread.sleep(1000);

        // Get location of item 40
        Point item40Location = getTreeNode("Node 40").getLocation();

        // Right click on item 45
        WebElement item45 = getTreeNode("Node 45");
        new Actions(getDriver()).moveToElement(item45).contextClick(item45)
                .perform();

        // Ensure location of item 40 is still the same (no scrolling)
        Point item40Location2 = getTreeNode("Node 40").getLocation();
        assertEquals(item40Location.getY(), item40Location2.getY());
    }

    private WebElement getTreeNode(String caption) {
        return getDriver()
                .findElement(By.xpath("//span[text() = '" + caption + "']"));
    }
}
