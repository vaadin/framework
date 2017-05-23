package com.vaadin.tests.components.tree;

import java.io.IOException;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TreeElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeBasicFeaturesTest extends MultiBrowserTest {

    private static final Predicate<TestBenchElement> THEME_RESOURCE = e -> {
        return e.isElementPresent(By.tagName("img"))
                && e.findElement(By.tagName("img")).getAttribute("src")
                        .contains("bullet.png");
    };
    private static final Predicate<TestBenchElement> VAADIN_ICON = e -> {
        return e.isElementPresent(By.className("v-icon"))
                && e.findElement(By.className("v-icon")).getAttribute("class")
                        .contains("Vaadin-Icons");
    };
    private static final Predicate<TestBenchElement> CLASS_RESOURCE = e -> {
        return e.isElementPresent(By.tagName("img"))
                && e.findElement(By.tagName("img")).getAttribute("src")
                        .contains("m.gif");
    };

    @Before
    public void before() {
        setDebug(true);
        openTestURL();
    }

    @Test
    public void tree_expand_and_collapse() {
        TreeElement tree = $(TreeElement.class).first();
        tree.expand(0);
        Assert.assertEquals("1 | 0", tree.getItem(1).getText());
        tree.collapse(0);
        Assert.assertEquals("0 | 1", tree.getItem(1).getText());
        assertNoErrorNotifications();
    }

    @Test
    public void tree_expand_all() throws IOException {
        expandAll();
        assertAllExpanded(false);
        assertNoErrorNotifications();
    }

    @Test
    public void tree_expand_all_with_icons() throws IOException {
        selectMenuPath("Component", "Icons", "By Depth");
        Assert.assertTrue("Icon not present", $(TreeElement.class).first()
                .getItem(0).isElementPresent(By.tagName("img")));
        expandAll();
        assertAllExpanded(true);
        assertNoErrorNotifications();
    }

    private void expandAll() {
        TreeElement tree = $(TreeElement.class).first();
        for (int i = 0; i < 2; ++i) {
            int max = tree.getAllItems().size();
            for (int j = 1; j <= max; ++j) {
                if (tree.isExpanded(max - j)) {
                    continue;
                }
                tree.expand(max - j);
            }
        }
    }

    private void assertAllExpanded(boolean shouldHaveIcon) {
        TreeElement tree = $(TreeElement.class).first();
        TestBenchElement item;
        int n = 0;
        for (int i = 0; i < 3; ++i) {
            item = tree.getItem(n++);
            Assert.assertEquals("0 | " + i, item.getText());

            Assert.assertEquals("Unexpected icon state", shouldHaveIcon,
                    THEME_RESOURCE.test(item));

            for (int j = 0; j < 3; ++j) {
                item = tree.getItem(n++);
                Assert.assertEquals((shouldHaveIcon ? "\ue92d" : "") + "1 | " + j,
                        item.getText());

                Assert.assertEquals("Unexpected icon state", shouldHaveIcon,
                        VAADIN_ICON.test(item));

                for (int k = 0; k < 3; ++k) {
                    item = tree.getItem(n++);
                    Assert.assertEquals("2 | " + k, item.getText());

                    Assert.assertEquals("Unexpected icon state", shouldHaveIcon,
                            CLASS_RESOURCE.test(item));
                }
            }
        }
    }

    @Test
    public void tree_custom_caption() {
        selectMenuPath("Component", "Captions", "Custom caption");
        TreeElement tree = $(TreeElement.class).first();
        Assert.assertEquals("Id: /0/0, Depth: 0, Index: 0",
                tree.getItem(0).getText());
        Assert.assertEquals("Id: /0/1, Depth: 0, Index: 1",
                tree.getItem(1).getText());
        tree.expand(0);
        Assert.assertEquals("Id: /0/0/1/0, Depth: 1, Index: 0",
                tree.getItem(1).getText());
        Assert.assertEquals("Id: /0/0/1/1, Depth: 1, Index: 1",
                tree.getItem(2).getText());
        tree.expand(1);
        Assert.assertEquals("Id: /0/0/1/0/2/0, Depth: 2, Index: 0",
                tree.getItem(2).getText());
        Assert.assertEquals("Id: /0/0/1/0/2/1, Depth: 2, Index: 1",
                tree.getItem(3).getText());

        assertNoErrorNotifications();
    }

    @Test
    public void tree_item_click() {
        selectMenuPath("Component", "Item Click Listener");
        $(TreeElement.class).first().getItem(1).click();
        Assert.assertTrue(logContainsText("ItemClick: 0 | 1"));
    }

    @Test
    public void tree_style_generator() {
        selectMenuPath("Component", "Style Generator");
        TreeElement tree = $(TreeElement.class).first();
        Assert.assertTrue("Style name not present",
                tree.wrap(TreeGridElement.class).getRow(0).getAttribute("class")
                        .contains("level0"));
        tree.expand(0);
        Assert.assertTrue("Style name not present",
                tree.wrap(TreeGridElement.class).getRow(1).getAttribute("class")
                        .contains("level1"));
        tree.expand(1);
        Assert.assertTrue("Style name not present",
                tree.wrap(TreeGridElement.class).getRow(2).getAttribute("class")
                        .contains("level2"));
    }

    @Test
    public void tree_disable_collapse() {
        selectMenuPath("Component", "Collapse Allowed");
        TreeElement tree = $(TreeElement.class).first();
        tree.expand(0);
        tree.expand(1);
        Assert.assertEquals("2 | 0", tree.getItem(2).getText());
        tree.collapse(1);
        Assert.assertEquals("Tree should prevent collapsing all nodes.",
                "2 | 0", tree.getItem(2).getText());
    }

    @Test
    public void tree_multiselect() {
        selectMenuPath("Component", "Selection Mode", "MULTI");
        TreeElement tree = $(TreeElement.class).first();
        tree.getItem(0).click();
        TreeGridElement wrap = tree.wrap(TreeGridElement.class);
        Assert.assertFalse(
                "Tree MultiSelection shouldn't have selection column",
                wrap.getCell(0, 0).isElementPresent(By.tagName("input")));
        Assert.assertTrue("First row was not selected",
                wrap.getRow(0).isSelected());
        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN, Keys.SPACE)
                .perform();
        Assert.assertTrue("First row was deselected",
                wrap.getRow(0).isSelected());
        Assert.assertTrue("Second row was not selected",
                wrap.getRow(1).isSelected());
    }

    @Test
    public void tree_multiselect_click() {
        selectMenuPath("Component", "Selection Mode", "MULTI");
        TreeElement tree = $(TreeElement.class).first();
        TreeGridElement wrap = tree.wrap(TreeGridElement.class);
        tree.getItem(0).click();
        Assert.assertTrue("First row was not selected",
                wrap.getRow(0).isSelected());
        tree.getItem(1).click();
        Assert.assertTrue("First row was deselected",
                wrap.getRow(0).isSelected());
        Assert.assertTrue("Second row was not selected",
                wrap.getRow(1).isSelected());
        tree.getItem(0).click();
        Assert.assertFalse("First row was not deselected",
                wrap.getRow(0).isSelected());
    }
}
