package com.vaadin.tests.components.treegrid;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeGridBigDetailsManagerTest extends MultiBrowserTest {

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
    private static final String SCROLL_TO_55 = "scrollTo55";
    private static final String SCROLL_TO_3055 = "scrollTo3055";
    private static final String SCROLL_TO_END = "scrollToEnd";
    private static final String SCROLL_TO_START = "scrollToStart";
    private static final String TOGGLE_15 = "toggle15";
    private static final String TOGGLE_3000 = "toggle3000";

    private TreeGridElement treeGrid;
    private int expectedSpacerHeight = 0;
    private int expectedRowHeight = 0;

    private ExpectedCondition<Boolean> expectedConditionDetails(final int root,
            final Integer branch, final Integer leaf) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver arg0) {
                return getSpacer(root, branch, leaf) != null;
            }

            @Override
            public String toString() {
                // waiting for...
                if (leaf != null) {
                    return String.format(
                            "Leaf %s/%s/%s details row contents to be found",
                            root, branch, leaf);
                }
                return String.format(
                        "Branch %s/%s details row contents to be found", root,
                        branch);
            }
        };
    }

    private WebElement getSpacer(final int root, final Integer branch,
            final Integer leaf) {
        String text;
        if (leaf == null) {
            if (branch == null) {
                text = "details for Root %s";
            } else {
                text = "details for Branch %s/%s";
            }
        } else {
            text = "details for Leaf %s/%s/%s";
        }
        try {
            List<WebElement> spacers = treeGrid
                    .findElements(By.className(CLASSNAME_SPACER));
            for (WebElement spacer : spacers) {
                List<WebElement> labels = spacer
                        .findElements(By.className(CLASSNAME_LABEL));
                for (WebElement label : labels) {
                    if (String.format(text, root, branch, leaf)
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

    private WebElement getRow(int index) {
        return treeGrid.getBody().findElements(By.className("v-treegrid-row"))
                .get(index);
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
            // -1 should be enough, but increased tolerance to -3 for FireFox
            // and IE11 since a few pixels' discrepancy isn't relevant for this
            // fix
            assertThat("Unexpected spacer position.", spacer.getLocation().y,
                    greaterThanOrEqualTo(previousSpacer.getLocation().y
                            + expectedSpacerHeight + expectedRowHeight - 3));
            previousSpacer = spacer;
        }
    }

    private void assertNoErrors() {
        assertEquals("Error notification detected.", 0,
                treeGrid.findElements(By.className(CLASSNAME_ERROR)).size());
    }

    private void addGrid() {
        $(ButtonElement.class).id(ADD_GRID).click();
        waitForElementPresent(By.className(CLASSNAME_TREEGRID));

        treeGrid = $(TreeGridElement.class).first();
        expectedRowHeight = treeGrid.getRow(0).getSize().getHeight();
    }

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void expandAllOpenAllInitialDetails_toggleOneTwice_hideAll() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        addGrid();

        waitUntil(expectedConditionDetails(0, 0, 0));
        ensureExpectedSpacerHeightSet();
        int spacerCount = treeGrid.findElements(By.className(CLASSNAME_SPACER))
                .size();
        assertSpacerPositions();

        treeGrid.collapseWithClick(0);

        // collapsing one shouldn't affect spacer count, just update the cache
        waitUntil(ExpectedConditions.not(expectedConditionDetails(0, 0, 0)));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        treeGrid.expandWithClick(0);

        // expanding back shouldn't affect spacer count, just update the cache
        waitUntil(expectedConditionDetails(0, 0, 0));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        // test that repeating the toggle still doesn't change anything
        treeGrid.collapseWithClick(0);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(0, 0, 0)));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        treeGrid.expandWithClick(0);

        waitUntil(expectedConditionDetails(0, 0, 0));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        // test that hiding all still won't break things
        $(ButtonElement.class).id(HIDE_DETAILS).click();
        waitForElementNotPresent(By.className(CLASSNAME_SPACER));

        assertNoErrors();
    }

    @Test
    public void expandAllOpenAllInitialDetails_toggleAll() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        addGrid();

        waitUntil(expectedConditionDetails(0, 0, 0));
        ensureExpectedSpacerHeightSet();

        int spacerCount = treeGrid.findElements(By.className(CLASSNAME_SPACER))
                .size();
        assertSpacerPositions();

        $(ButtonElement.class).id(COLLAPSE_ALL).click();

        // There should still be a full cache's worth of details rows open,
        // just not the same rows than before collapsing all.
        waitForElementNotPresent(By.className(CLASSNAME_LEAF));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        // FIXME: TreeGrid fails to update cache correctly when you expand all
        // and triggers client-side exceptions for rows that fall outside of the
        // cache because they try to extend the cache with a range that isn't
        // connected to the cached range
        if (true) {// remove this block after fixed
            return;
        }

        $(ButtonElement.class).id(EXPAND_ALL).click();

        // State should have returned to what it was before collapsing.
        waitUntil(expectedConditionDetails(0, 0, 0), 15);
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        assertNoErrors();
    }

    @Test
    public void expandAllOpenNoInitialDetails_showSeveral_toggleOneByOne() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        addGrid();

        // open details for several rows, leave one out from the hierarchy that
        // is to be collapsed
        treeGrid.getCell(0, 0).click();
        treeGrid.getCell(1, 0).click();
        treeGrid.getCell(2, 0).click();
        // no click for cell (3, 0)
        treeGrid.getCell(4, 0).click();
        treeGrid.getCell(5, 0).click();
        treeGrid.getCell(6, 0).click();
        treeGrid.getCell(7, 0).click();
        treeGrid.getCell(8, 0).click();
        int spacerCount = 8;

        waitUntil(expectedConditionDetails(0, 0, 0));
        assertSpacerCount(spacerCount);
        ensureExpectedSpacerHeightSet();
        assertSpacerPositions();

        // toggle the root with open details rows
        treeGrid.collapseWithClick(0);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(0, 0, 0)));
        assertSpacerCount(1);
        assertSpacerHeights();

        treeGrid.expandWithClick(0);

        waitUntil(expectedConditionDetails(0, 0, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        // toggle one of the branches with open details rows
        treeGrid.collapseWithClick(5);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(0, 1, 0)));
        assertSpacerCount(spacerCount - 3);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(5);

        waitUntil(expectedConditionDetails(0, 1, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        assertNoErrors();
    }

    @Test
    public void expandAllOpenAllInitialDetails_hideOne() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        addGrid();

        // check the position of a row
        int oldY = treeGrid.getCell(2, 0).getLocation().getY();

        // hide the spacer from previous row
        treeGrid.getCell(1, 0).click();

        // ensure the investigated row moved
        assertNotEquals(oldY, treeGrid.getCell(2, 0).getLocation().getY());
    }

    @Test
    public void expandAllOpenAllInitialDetailsScrolled_toggleOne_hideAll() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        addGrid();

        $(ButtonElement.class).id(SCROLL_TO_55).click();

        waitUntil(expectedConditionDetails(1, 2, 0));
        ensureExpectedSpacerHeightSet();
        int spacerCount = treeGrid.findElements(By.className(CLASSNAME_SPACER))
                .size();
        assertSpacerPositions();

        treeGrid.collapseWithClick(50);

        // collapsing one shouldn't affect spacer count, just update the cache
        waitUntil(ExpectedConditions.not(expectedConditionDetails(1, 2, 0)));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        treeGrid.expandWithClick(50);

        // expanding back shouldn't affect spacer count, just update the cache
        waitUntil(expectedConditionDetails(1, 2, 0));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        // test that repeating the toggle still doesn't change anything

        treeGrid.collapseWithClick(50);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(1, 2, 0)));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        treeGrid.expandWithClick(50);

        waitUntil(expectedConditionDetails(1, 2, 0));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        // test that hiding all still won't break things

        $(ButtonElement.class).id(HIDE_DETAILS).click();
        waitForElementNotPresent(By.className(CLASSNAME_SPACER));

        assertNoErrors();
    }

    @Test
    public void expandAllOpenAllInitialDetailsScrolled_toggleAll() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        addGrid();

        $(ButtonElement.class).id(SCROLL_TO_55).click();

        waitUntil(expectedConditionDetails(1, 3, 0));
        ensureExpectedSpacerHeightSet();

        int spacerCount = treeGrid.findElements(By.className(CLASSNAME_SPACER))
                .size();
        assertSpacerPositions();

        $(ButtonElement.class).id(COLLAPSE_ALL).click();

        waitForElementNotPresent(By.className(CLASSNAME_LEAF));

        // There should still be a full cache's worth of details rows open,
        // just not the same rows than before collapsing all.
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        // FIXME: collapsing and expanding too many rows after scrolling still
        // fails to reset to the same state
        if (true) { // remove this block after fixed
            return;
        }

        $(ButtonElement.class).id(EXPAND_ALL).click();

        // State should have returned to what it was before collapsing.
        waitUntil(expectedConditionDetails(1, 3, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        assertNoErrors();
    }

    @Test
    public void expandAllOpenNoInitialDetailsScrolled_showSeveral_toggleOneByOne() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        addGrid();

        $(ButtonElement.class).id(SCROLL_TO_55).click();

        assertSpacerCount(0);

        // open details for several rows, leave one out from the hierarchy that
        // is to be collapsed
        treeGrid.getCell(50, 0).click(); // Branch 1/2
        treeGrid.getCell(51, 0).click(); // Leaf 1/2/0
        treeGrid.getCell(52, 0).click(); // Leaf 1/2/1
        // no click for cell (53, 0) // Leaf 1/2/2
        treeGrid.getCell(54, 0).click(); // Branch 1/3
        treeGrid.getCell(55, 0).click(); // Leaf 1/3/0
        treeGrid.getCell(56, 0).click(); // Leaf 1/3/1
        treeGrid.getCell(57, 0).click(); // Leaf 1/3/2
        treeGrid.getCell(58, 0).click(); // Branch 1/4
        int spacerCount = 8;

        waitUntil(expectedConditionDetails(1, 2, 0));
        assertSpacerCount(spacerCount);
        ensureExpectedSpacerHeightSet();
        assertSpacerPositions();

        // toggle the branch with partially open details rows
        treeGrid.collapseWithClick(50);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(1, 2, 0)));
        assertSpacerCount(spacerCount - 2);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(50);

        waitUntil(expectedConditionDetails(1, 2, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        // toggle the branch with fully open details rows
        treeGrid.collapseWithClick(54);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(1, 3, 0)));
        assertSpacerCount(spacerCount - 3);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(54);

        waitUntil(expectedConditionDetails(1, 3, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        // repeat both toggles to ensure still no errors
        treeGrid.collapseWithClick(50);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(1, 2, 0)));
        assertSpacerCount(spacerCount - 2);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(50);

        waitUntil(expectedConditionDetails(1, 2, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();
        treeGrid.collapseWithClick(54);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(1, 3, 0)));
        assertSpacerCount(spacerCount - 3);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(54);

        waitUntil(expectedConditionDetails(1, 3, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        assertNoErrors();
    }

    @Test
    public void expandAllOpenAllInitialDetailsScrolled_hideOne() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        addGrid();

        $(ButtonElement.class).id(SCROLL_TO_55).click();

        // check the position of a row
        int oldY = treeGrid.getCell(52, 0).getLocation().getY();

        // hide the spacer from previous row
        treeGrid.getCell(51, 0).click();

        // ensure the investigated row moved
        assertNotEquals(oldY, treeGrid.getCell(52, 0).getLocation().getY());
    }

    @Test
    public void expandAllOpenAllInitialDetailsScrolledFar_toggleOne_hideAll() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        addGrid();

        $(ButtonElement.class).id(SCROLL_TO_3055).click();

        waitUntil(expectedConditionDetails(74, 4, 0));
        ensureExpectedSpacerHeightSet();
        int spacerCount = treeGrid.findElements(By.className(CLASSNAME_SPACER))
                .size();
        assertSpacerPositions();

        treeGrid.collapseWithClick(3051);

        // collapsing one shouldn't affect spacer count, just update the cache
        waitUntil(ExpectedConditions.not(expectedConditionDetails(1, 2, 0)));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        treeGrid.expandWithClick(3051);

        // expanding back shouldn't affect spacer count, just update the cache
        waitUntil(expectedConditionDetails(74, 4, 0));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        // test that repeating the toggle still doesn't change anything

        treeGrid.collapseWithClick(3051);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(74, 4, 0)));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        treeGrid.expandWithClick(3051);

        waitUntil(expectedConditionDetails(74, 4, 0));
        assertSpacerHeights();
        assertSpacerPositions();
        assertSpacerCount(spacerCount);

        // test that hiding all still won't break things

        $(ButtonElement.class).id(HIDE_DETAILS).click();
        waitForElementNotPresent(By.className(CLASSNAME_SPACER));

        assertNoErrors();
    }

    @Test
    public void expandAllOpenAllInitialDetailsScrolledFar_toggleAll() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        addGrid();

        $(ButtonElement.class).id(SCROLL_TO_3055).click();

        waitUntil(expectedConditionDetails(74, 4, 0));
        ensureExpectedSpacerHeightSet();

        int spacerCount = treeGrid.findElements(By.className(CLASSNAME_SPACER))
                .size();
        assertSpacerPositions();

        $(ButtonElement.class).id(COLLAPSE_ALL).click();

        waitForElementNotPresent(By.className(CLASSNAME_LEAF));

        // There should still be a full cache's worth of details rows open,
        // just not the same rows than before collapsing all.
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        // FIXME: collapsing and expanding too many rows after scrolling still
        // fails to reset to the same state
        if (true) { // remove this block after fixed
            return;
        }

        $(ButtonElement.class).id(EXPAND_ALL).click();

        // State should have returned to what it was before collapsing.
        waitUntil(expectedConditionDetails(74, 4, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        assertNoErrors();
    }

    @Test
    public void expandAllOpenNoInitialDetailsScrolledFar_showSeveral_toggleOneByOne() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        addGrid();

        $(ButtonElement.class).id(SCROLL_TO_3055).click();

        assertSpacerCount(0);

        // open details for several rows, leave one out from the hierarchy that
        // is to be collapsed
        treeGrid.getCell(3051, 0).click(); // Branch 74/4
        treeGrid.getCell(3052, 0).click(); // Leaf 74/4/0
        treeGrid.getCell(3053, 0).click(); // Leaf 74/4/1
        // no click for cell (3054, 0) // Leaf 74/4/2
        treeGrid.getCell(3055, 0).click(); // Branch 74/5
        treeGrid.getCell(3056, 0).click(); // Leaf 74/5/0
        treeGrid.getCell(3057, 0).click(); // Leaf 74/5/1
        treeGrid.getCell(3058, 0).click(); // Leaf 74/5/2
        treeGrid.getCell(3059, 0).click(); // Branch 74/6
        int spacerCount = 8;

        waitUntil(expectedConditionDetails(74, 4, 0));
        assertSpacerCount(spacerCount);
        ensureExpectedSpacerHeightSet();
        assertSpacerPositions();

        // toggle the branch with partially open details rows
        treeGrid.collapseWithClick(3051);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(74, 4, 0)));
        assertSpacerCount(spacerCount - 2);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(3051);

        waitUntil(expectedConditionDetails(74, 4, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        // toggle the branch with fully open details rows
        treeGrid.collapseWithClick(3055);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(74, 5, 0)));
        assertSpacerCount(spacerCount - 3);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(3055);

        waitUntil(expectedConditionDetails(74, 5, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        // repeat both toggles to ensure still no errors
        treeGrid.collapseWithClick(3051);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(74, 4, 0)));
        assertSpacerCount(spacerCount - 2);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(3051);

        waitUntil(expectedConditionDetails(74, 4, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();
        treeGrid.collapseWithClick(3055);

        waitUntil(ExpectedConditions.not(expectedConditionDetails(74, 5, 0)));
        assertSpacerCount(spacerCount - 3);
        assertSpacerHeights();
        assertSpacerPositions();

        treeGrid.expandWithClick(3055);

        waitUntil(expectedConditionDetails(74, 5, 0));
        assertSpacerCount(spacerCount);
        assertSpacerHeights();
        assertSpacerPositions();

        assertNoErrors();
    }

    @Test
    public void expandAllOpenAllInitialDetailsScrolledFar_hideOne() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        addGrid();

        $(ButtonElement.class).id(SCROLL_TO_3055).click();

        // check the position of a row
        int oldY = treeGrid.getCell(3052, 0).getLocation().getY();

        // hide the spacer from previous row
        treeGrid.getCell(3051, 0).click();

        // ensure the investigated row moved
        assertNotEquals(oldY, treeGrid.getCell(52, 0).getLocation().getY());
    }

    @Test
    public void expandAllOpenAllInitialDetails_checkScrollPositions() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        $(ButtonElement.class).id(SHOW_DETAILS).click();
        addGrid();

        TestBenchElement tableWrapper = treeGrid.getTableWrapper();

        $(ButtonElement.class).id(SCROLL_TO_55).click();
        waitUntil(expectedConditionDetails(1, 3, 0));

        WebElement detailsRow = getSpacer(1, 3, 0);
        assertNotNull("Spacer for row 55 not found", detailsRow);

        int wrapperY = tableWrapper.getLocation().getY();
        int wrapperHeight = tableWrapper.getSize().getHeight();

        int detailsY = detailsRow.getLocation().getY();
        int detailsHeight = detailsRow.getSize().getHeight();

        assertThat("Scroll to 55 didn't scroll as expected",
                (double) detailsY + detailsHeight,
                closeTo(wrapperY + wrapperHeight, 2d));

        $(ButtonElement.class).id(SCROLL_TO_3055).click();
        waitUntil(expectedConditionDetails(74, 5, null));

        detailsRow = getSpacer(74, 5, null);
        assertNotNull("Spacer for row 3055 not found", detailsRow);

        detailsY = detailsRow.getLocation().getY();
        detailsHeight = detailsRow.getSize().getHeight();

        assertThat("Scroll to 3055 didn't scroll as expected",
                (double) detailsY + detailsHeight,
                closeTo(wrapperY + wrapperHeight, 2d));

        $(ButtonElement.class).id(SCROLL_TO_END).click();
        waitUntil(expectedConditionDetails(99, 9, 2));

        detailsRow = getSpacer(99, 9, 2);
        assertNotNull("Spacer for last row not found", detailsRow);

        detailsY = detailsRow.getLocation().getY();
        detailsHeight = detailsRow.getSize().getHeight();

        // the layout jumps sometimes, check again
        wrapperY = tableWrapper.getLocation().getY();
        wrapperHeight = tableWrapper.getSize().getHeight();

        assertThat("Scroll to end didn't scroll as expected",
                (double) detailsY + detailsHeight,
                closeTo(wrapperY + wrapperHeight, 2d));

        $(ButtonElement.class).id(SCROLL_TO_START).click();
        waitUntil(expectedConditionDetails(0, 0, 0));

        WebElement firstRow = getRow(0);
        TestBenchElement header = treeGrid.getHeader();

        assertThat("Scroll to start didn't scroll as expected",
                (double) firstRow.getLocation().getY(),
                closeTo(wrapperY + header.getSize().getHeight(), 1d));
    }

    @Test
    public void expandAllOpenNoInitialDetails_testToggleScrolling() {
        $(ButtonElement.class).id(EXPAND_ALL).click();
        addGrid();

        TestBenchElement tableWrapper = treeGrid.getTableWrapper();
        int wrapperY = tableWrapper.getLocation().getY();

        WebElement firstRow = getRow(0);
        int firstRowY = firstRow.getLocation().getY();

        TestBenchElement header = treeGrid.getHeader();
        int headerHeight = header.getSize().getHeight();

        assertThat("Unexpected initial scroll position", (double) firstRowY,
                closeTo(wrapperY + headerHeight, 1d));

        $(ButtonElement.class).id(TOGGLE_15).click();

        firstRowY = firstRow.getLocation().getY();

        assertThat(
                "Toggling row 15's details open should have caused scrolling",
                (double) firstRowY, not(closeTo(wrapperY + headerHeight, 1d)));

        $(ButtonElement.class).id(SCROLL_TO_START).click();

        firstRowY = firstRow.getLocation().getY();

        assertThat("Scrolling to start failed", (double) firstRowY,
                closeTo(wrapperY + headerHeight, 1d));

        $(ButtonElement.class).id(TOGGLE_15).click();

        firstRowY = firstRow.getLocation().getY();

        assertThat(
                "Toggling row 15's details closed should not have caused scrolling",
                (double) firstRowY, closeTo(wrapperY + headerHeight, 1d));

        $(ButtonElement.class).id(TOGGLE_3000).click();

        firstRowY = firstRow.getLocation().getY();

        assertThat(
                "Toggling row 3000's details open should not have caused scrolling",
                (double) firstRowY, closeTo(wrapperY + headerHeight, 1d));

        $(ButtonElement.class).id(SCROLL_TO_55).click();

        WebElement row = getRow(0);
        assertNotEquals("First row should be out of visual range", firstRowY,
                row);

        $(ButtonElement.class).id(TOGGLE_15).click();

        assertEquals(
                "Toggling row 15's details open should not have caused scrolling "
                        + "when row 15 is outside of visual range",
                row, getRow(0));
    }

}
