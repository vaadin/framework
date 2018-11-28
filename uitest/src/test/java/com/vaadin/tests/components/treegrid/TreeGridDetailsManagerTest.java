package com.vaadin.tests.components.treegrid;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeGridDetailsManagerTest extends MultiBrowserTest {

    private static final String CLASSNAME_ERROR = "v-Notification-error";
    private static final String CLASSNAME_LABEL = "v-label";
    private static final String CLASSNAME_LEAF = "v-treegrid-row-depth-1";
    private static final String CLASSNAME_SPACER = "v-treegrid-spacer";
    private static final String CLASSNAME_TREEGRID = "v-treegrid";

    private static final String EXPAND_ALL = "expandAll";
    private static final String COLLAPSE_ALL = "collapseAll";
    private static final String SHOW_DETAILS = "showDetails";
    private static final String HIDE_DETAILS = "hideDetails";
    private static final String ADD_GRID = "addGrid";

    private TreeGridElement treeGrid;
    private int expectedSpacerHeight = 0;
    private int expectedRowHeight = 0;

    private ExpectedCondition<Boolean> expectedConditionDetails(final int root,
            final int leaf) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver arg0) {
                return getSpacer(root, leaf) != null;
            }

            @Override
            public String toString() {
                // waiting for...
                return String.format(
                        "Leaf %s/%s details row contents to be found", root,
                        leaf);
            }
        };
    }

    private WebElement getSpacer(final int root, final Integer leaf) {
        String text;
        if (leaf == null) {
            text = "details for Root %s";
        } else {
            text = "details for Leaf %s/%s";
        }
        try {
            List<WebElement> spacers = treeGrid
                    .findElements(By.className(CLASSNAME_SPACER));
            for (WebElement spacer : spacers) {
                List<WebElement> labels = spacer
                        .findElements(By.className(CLASSNAME_LABEL));
                for (WebElement label : labels) {
                    if (String.format(text, root, leaf)
                            .equals(label.getText())) {
                        return spacer;
                    }
                }
            }
        } catch (StaleElementReferenceException e) {
            treeGrid = $(TreeGridElement.class).first();
        }
        return null;
    }

    private void ensureExpectedSpacerHeightSet() {
        if (expectedSpacerHeight == 0) {
            expectedSpacerHeight = treeGrid
                    .findElement(By.className(CLASSNAME_SPACER)).getSize()
                    .getHeight();
            assertThat((double) expectedSpacerHeight, closeTo(27d, 2d));
        }
    }

    private void assertSpacerCount(int expectedSpacerCount) {
        assertEquals("Unexpected amount of spacers.", expectedSpacerCount,
                treeGrid.findElements(By.className(CLASSNAME_SPACER)).size());
    }

    /**
     * Asserts that every spacer has the same height.
     */
    private void assertSpacerHeights() {
        List<WebElement> spacers = treeGrid
                .findElements(By.className(CLASSNAME_SPACER));
        for (WebElement spacer : spacers) {
            assertEquals("Unexpected spacer height.", expectedSpacerHeight,
                    spacer.getSize().getHeight());
        }
    }

    /**
     * Asserts that every spacer is at least a row height from the previous one.
     * Doesn't check that the spacers are in correct order or rendered properly.
     */
    private void assertSpacerPositions() {
        List<WebElement> spacers = treeGrid
                .findElements(By.className(CLASSNAME_SPACER));
        WebElement previousSpacer = null;
        for (WebElement spacer : spacers) {
            if (previousSpacer == null) {
                previousSpacer = spacer;
                continue;
            }
            assertThat("Unexpected spacer position.", spacer.getLocation().y,
                    greaterThanOrEqualTo(previousSpacer.getLocation().y
                            + expectedSpacerHeight + expectedRowHeight - 1));
            previousSpacer = spacer;
        }
    }

    private void assertNoErrors() {
        assertEquals("Error notification detected.", 0,
                treeGrid.findElements(By.className(CLASSNAME_ERROR)).size());
    }

    @Test
    public void expandAllOpenAllInitialDetails_toggleOne_hideAll() {
        openTestURL();
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        $(ButtonElement.class).id(ADD_GRID).click();

        waitForElementPresent(By.className(CLASSNAME_TREEGRID));

        treeGrid = $(TreeGridElement.class).first();
        int spacerCount = 6;

        waitUntil(expectedConditionDetails(0, 0));
        assertSpacerCount(spacerCount);
        ensureExpectedSpacerHeightSet();
        assertSpacerPositions();

        // toggle one root
        treeGrid.collapseWithClick(0);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(0, 0)));
        assertSpacerCount(spacerCount - 2);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(0);

        waitUntil(expectedConditionDetails(0, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        // test that repeating the toggle still doesn't change anything
        treeGrid.collapseWithClick(0);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(0, 0)));
        assertSpacerCount(spacerCount - 2);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(0);

        waitUntil(expectedConditionDetails(0, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        // test that hiding all still won't break things
        $(ButtonElement.class).id(HIDE_DETAILS).click();
        waitForElementNotPresent(By.className(CLASSNAME_SPACER));

        assertNoErrors();
    }

    @Test
    public void expandAllOpenAllInitialDetails_toggleAll() {
        openTestURL();
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        $(ButtonElement.class).id(ADD_GRID).click();

        waitForElementPresent(By.className(CLASSNAME_TREEGRID));

        treeGrid = $(TreeGridElement.class).first();
        int spacerCount = 6;

        waitUntil(expectedConditionDetails(0, 0));
        assertSpacerCount(spacerCount);
        ensureExpectedSpacerHeightSet();
        assertSpacerPositions();

        $(ButtonElement.class).id(COLLAPSE_ALL).click();

        waitForElementNotPresent(By.className(CLASSNAME_LEAF));
        assertSpacerCount(2);
        assertSpacerHeights();
        assertSpacerPositions();

        $(ButtonElement.class).id(EXPAND_ALL).click();

        waitUntil(expectedConditionDetails(0, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        // test that repeating the toggle still doesn't change anything
        $(ButtonElement.class).id(COLLAPSE_ALL).click();

        waitForElementNotPresent(By.className(CLASSNAME_LEAF));
        assertSpacerCount(2);
        assertSpacerHeights();
        assertSpacerPositions();

        $(ButtonElement.class).id(EXPAND_ALL).click();

        waitUntil(expectedConditionDetails(0, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        assertNoErrors();
    }

    @Test
    public void expandAllOpenNoInitialDetails_showAlmostAll_toggleOneByOne() {
        openTestURL();
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(ADD_GRID).click();

        waitForElementPresent(By.className(CLASSNAME_TREEGRID));

        treeGrid = $(TreeGridElement.class).first();

        // expand almost all rows, leave one out from the hierarchy that is to
        // be collapsed
        treeGrid.getCell(0, 0).click();
        treeGrid.getCell(1, 0).click();
        treeGrid.getCell(3, 0).click();
        treeGrid.getCell(4, 0).click();
        treeGrid.getCell(5, 0).click();
        int spacerCount = 5;

        waitUntil(expectedConditionDetails(0, 0));
        assertSpacerCount(spacerCount);
        ensureExpectedSpacerHeightSet();
        assertSpacerPositions();

        treeGrid.collapseWithClick(0);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(0, 0)));
        assertSpacerCount(spacerCount - 1);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(0);

        waitUntil(expectedConditionDetails(0, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();
        assertNotNull(getSpacer(1, 0));

        treeGrid.collapseWithClick(3);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(1, 0)));
        assertSpacerCount(spacerCount - 2);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(3);

        waitUntil(expectedConditionDetails(1, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        assertNoErrors();
    }

}
