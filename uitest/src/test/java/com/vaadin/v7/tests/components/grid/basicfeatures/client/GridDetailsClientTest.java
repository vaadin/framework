package com.vaadin.v7.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.shared.Range;
import com.vaadin.testbench.By;
import com.vaadin.testbench.ElementQuery;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.v7.shared.ui.grid.ScrollDestination;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridDetailsClientTest extends GridBasicClientFeaturesTest {

    private static final String[] SET_GENERATOR = { "Component", "Row details",
            "Set generator" };
    private static final String[] SET_FAULTY_GENERATOR = { "Component",
            "Row details", "Set faulty generator" };
    private static final String[] SET_EMPTY_GENERATOR = { "Component",
            "Row details", "Set empty generator" };

    @Before
    public void setUp() {
        setDebug(true);
        openTestURL();
    }

    @Test(expected = NoSuchElementException.class)
    public void noDetailsByDefault() {
        assertNull("details for row 1 should not exist at the start",
                getGridElement().getDetails(1));
    }

    @Test(expected = NoSuchElementException.class)
    public void nullRendererDoesNotShowDetailsPlaceholder() {
        toggleDetailsFor(1);
        getGridElement().getDetails(1);
    }

    @Test
    public void applyRendererThenOpenDetails() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);

        TestBenchElement details = getGridElement().getDetails(1);
        assertTrue("Unexpected details content",
                details.getText().startsWith("Row: 1."));
    }

    @Test(expected = NoSuchElementException.class)
    public void openDetailsThenAppyRendererShouldNotShowDetails() {
        toggleDetailsFor(1);
        selectMenuPath(SET_GENERATOR);

        getGridElement().getDetails(1);
    }

    @Test
    public void openHiddenDetailsThenScrollToIt() {
        try {
            getGridElement().getDetails(100);
            fail("details row for 100 was apparently found, while it shouldn't have been.");
        } catch (NoSuchElementException e) {
            // expected
        }

        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(100);

        // scroll a bit beyond so we see below.
        getGridElement().scrollToRow(101);

        TestBenchElement details = getGridElement().getDetails(100);
        assertTrue("Unexpected details content",
                details.getText().startsWith("Row: 100."));
    }

    @Test
    public void errorUpdaterShowsErrorNotification() {
        assertFalse("No notifications should've been at the start",
                $(NotificationElement.class).exists());

        selectMenuPath(SET_FAULTY_GENERATOR);
        toggleDetailsFor(1);

        ElementQuery<NotificationElement> notification = $(
                NotificationElement.class);
        assertTrue("Was expecting an error notification here",
                notification.exists());
        notification.first().close();

        assertEquals("The error details element should be empty", "",
                getGridElement().getDetails(1).getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void detailsClosedWhenResettingGenerator() {

        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);

        selectMenuPath(SET_FAULTY_GENERATOR);
        getGridElement().getDetails(1);
    }

    @Test
    public void settingNewGeneratorStillWorksAfterError() {
        selectMenuPath(SET_FAULTY_GENERATOR);
        toggleDetailsFor(1);
        $(NotificationElement.class).first().close();
        toggleDetailsFor(1);

        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);

        assertNotEquals("New details should've been generated even after error",
                "", getGridElement().getDetails(1).getText());
    }

    @Test
    public void updaterRendersExpectedWidgets() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);

        TestBenchElement detailsElement = getGridElement().getDetails(1);
        assertNotNull(detailsElement.findElement(By.className("gwt-Label")));
        assertNotNull(detailsElement.findElement(By.className("gwt-Button")));
    }

    @Test
    public void widgetsInUpdaterWorkAsExpected() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);

        TestBenchElement detailsElement = getGridElement().getDetails(1);
        WebElement button = detailsElement
                .findElement(By.className("gwt-Button"));
        button.click();

        WebElement label = detailsElement
                .findElement(By.className("gwt-Label"));
        assertEquals("clicked", label.getText());
    }

    @Test
    public void emptyGenerator() {
        selectMenuPath(SET_EMPTY_GENERATOR);
        toggleDetailsFor(1);

        assertEquals("empty generator did not produce an empty details row", "",
                getGridElement().getDetails(1).getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void removeDetailsRow() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);
        toggleDetailsFor(1);

        getGridElement().getDetails(1);
    }

    @Test
    public void rowElementClassNames() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(0);
        toggleDetailsFor(1);

        List<WebElement> elements = getGridElement()
                .findElements(By.className("v-grid-spacer"));
        assertEquals("v-grid-spacer", elements.get(0).getAttribute("class"));
        assertEquals("v-grid-spacer stripe",
                elements.get(1).getAttribute("class"));
    }

    @Test
    public void scrollDownToRowWithDetails() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(100);
        scrollToRow(100, ScrollDestination.ANY);

        Range validScrollRange = Range.between(1691, 1706);
        assertTrue(validScrollRange.contains(getGridVerticalScrollPos()));
    }

    @Test
    public void scrollUpToRowWithDetails() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(100);
        scrollGridVerticallyTo(999999);
        scrollToRow(100, ScrollDestination.ANY);

        Range validScrollRange = Range.between(1981, 2001);
        assertTrue(validScrollRange.contains(getGridVerticalScrollPos()));
    }

    @Test
    public void cannotScrollBeforeTop() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(1);
        scrollToRow(0, ScrollDestination.END);
        assertEquals(0, getGridVerticalScrollPos());
    }

    @Test
    public void cannotScrollAfterBottom() {
        selectMenuPath(SET_GENERATOR);
        toggleDetailsFor(999);
        scrollToRow(999, ScrollDestination.START);

        Range expectedRange = Range.withLength(19671, 20);
        assertTrue(expectedRange.contains(getGridVerticalScrollPos()));
    }

    private void scrollToRow(int rowIndex, ScrollDestination destination) {
        selectMenuPath(new String[] { "Component", "State", "Scroll to...",
                "Row " + rowIndex + "...", "Destination " + destination });
    }

    private void toggleDetailsFor(int rowIndex) {
        selectMenuPath(new String[] { "Component", "Row details",
                "Toggle details for...", "Row " + rowIndex });
    }
}
