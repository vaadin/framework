package com.vaadin.tests.components.tree;

import java.io.IOException;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TreeElement;
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
    }

    @Test
    public void tree_expand_all() throws IOException {
        expandAll();
        assertAllExpanded(false);
    }

    @Test
    public void tree_expand_all_with_icons() throws IOException {
        selectMenuPath("Component", "Icons", "By Depth");
        Assert.assertTrue("Icon not present", $(TreeElement.class).first()
                .getItem(0).isElementPresent(By.tagName("img")));
        expandAll();
        assertAllExpanded(true);
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
                Assert.assertEquals((shouldHaveIcon ? " " : "") + "1 | " + j,
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
    }

}
