package com.vaadin.v7.tests.components.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SelectItemAfterRemoveTest extends MultiBrowserTest {

    @Test
    public void selectedItemIsSelected() {
        openTestURL();

        getSecondSpan().click();

        assertEquals(2, getNodes().size());
        assertTrue(getFirstNode().getAttribute("class")
                .contains("v-tree-node-selected"));
    }

    private WebElement getFirstNode() {
        return getNodes().get(0);
    }

    private List<WebElement> getNodes() {
        return findElements(By.className("v-tree-node-caption"));
    }

    private WebElement getSecondSpan() {
        for (WebElement e : getSpans()) {
            if (e.getText().equals("second")) {
                return e;
            }
        }

        return null;
    }

    private List<WebElement> getSpans() {
        return findElements(By.tagName("span"));
    }
}
