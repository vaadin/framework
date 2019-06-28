package com.vaadin.tests.contextclick;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TreeElement;

import static org.junit.Assert.assertEquals;

public class TreeContextClickTest extends AbstractContextClickTest {

    @Test
    public void testContextClickOnItem() {
        openTestURL();

        addOrRemoveTypedListener();

        List<WebElement> nodes = $(TreeElement.class).first()
                .findElements(By.className("v-tree-node"));

        contextClick(nodes.get(1));

        assertEquals("1. ContextClickEvent: Bar", getLogRow(0));

        contextClick(nodes.get(0));

        assertEquals("2. ContextClickEvent: Foo", getLogRow(0));
    }

    @Test
    public void testContextClickOnSubItem() {
        openTestURL();

        addOrRemoveTypedListener();

        List<WebElement> nodes = $(TreeElement.class).first()
                .findElements(By.className("v-tree-node"));

        WebElement target = nodes.get(1);
        new Actions(getDriver()).moveToElement(target, getXOffset(target, 10), getYOffset(target, 10)).click()
                .perform();

        nodes = $(TreeElement.class).first()
                .findElements(By.className("v-tree-node"));
        contextClick(nodes.get(2));

        assertEquals("1. ContextClickEvent: Baz", getLogRow(0));
    }

    @Test
    public void testContextClickOnEmptyArea() {
        openTestURL();

        addOrRemoveTypedListener();

        contextClick($(TreeElement.class).first(), 20, 100);

        assertEquals("1. ContextClickEvent: null", getLogRow(0));
    }
}
