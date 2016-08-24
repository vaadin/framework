package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.NotificationElement;

public class GridBasicDetailsTest extends GridBasicsTest {
    /**
     * The reason to why last item details wasn't selected is that since it will
     * exist only after the viewport has been scrolled into view, we wouldn't be
     * able to scroll that particular details row into view, making tests
     * awkward with two scroll commands back to back.
     */
    private static final int ALMOST_LAST_INDEX = 995;
    private static final String[] OPEN_ALMOST_LAST_ITEM_DETAILS = new String[] {
            "Component", "Details", "Open " + ALMOST_LAST_INDEX };
    private static final String[] OPEN_FIRST_ITEM_DETAILS = new String[] {
            "Component", "Details", "Open First" };
    private static final String[] TOGGLE_FIRST_ITEM_DETAILS = new String[] {
            "Component", "Details", "Toggle First" };
    private static final String[] DETAILS_GENERATOR_NULL = new String[] {
            "Component", "Details", "Generators", "NULL" };
    private static final String[] DETAILS_GENERATOR_WATCHING = new String[] {
            "Component", "Details", "Generators", "\"Watching\"" };
    private static final String[] DETAILS_GENERATOR_PERSISTING = new String[] {
            "Component", "Details", "Generators", "Persisting" };
    private static final String[] CHANGE_HIERARCHY = new String[] { "Component",
            "Details", "Generators", "- Change Component" };

    @Override
    @Before
    public void setUp() {
        openTestURL();
    }

    @Test(expected = NoSuchElementException.class)
    public void openWithNoGenerator() {
        try {
            getGridElement().getDetails(0);
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException ignore) {
            // expected
        }

        try {
            selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        } catch (NoSuchElementException e) {
            Assert.fail("Unable to set up details.");
        }

        getGridElement().getDetails(0);
    }

    @Test
    public void openVisiblePopulatedDetails() {
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        assertNotNull("details should've populated", getGridElement()
                .getDetails(0).findElement(By.className("v-widget")));
    }

    @Test(expected = NoSuchElementException.class)
    public void closeVisiblePopulatedDetails() {
        try {
            selectMenuPath(DETAILS_GENERATOR_WATCHING);
            selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
            selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        } catch (NoSuchElementException e) {
            Assert.fail("Unable to set up details.");
        }
        getGridElement().getDetails(0);
    }

    @Test
    public void openDetailsOutsideOfActiveRange() throws InterruptedException {
        getGridElement().scroll(10000);
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        getGridElement().scroll(0);
        assertNotNull("details should've been opened",
                getGridElement().getDetails(0));
    }

    @Test(expected = NoSuchElementException.class)
    public void closeDetailsOutsideOfActiveRange() {
        try {
            selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
            getGridElement().scroll(10000);
            selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
            getGridElement().scroll(0);
        } catch (NoSuchElementException e) {
            Assert.fail("Unable to set up details.");
        }
        getGridElement().getDetails(0);
    }

    @Test
    public void componentIsVisibleClientSide() {
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);

        TestBenchElement details = getGridElement().getDetails(0);
        assertNotNull("No widget detected inside details",
                details.findElement(By.className("v-widget")));
    }

    @Test
    public void openingDetailsTwice() {
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS); // open
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS); // close
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS); // open

        TestBenchElement details = getGridElement().getDetails(0);
        assertNotNull("No widget detected inside details",
                details.findElement(By.className("v-widget")));
    }

    @Test(expected = NoSuchElementException.class)
    public void scrollingDoesNotCreateAFloodOfDetailsRows() {
        try {
            selectMenuPath(DETAILS_GENERATOR_WATCHING);

            // scroll somewhere to hit uncached rows
            getGridElement().scrollToRow(101);
        } catch (NoSuchElementException e) {
            Assert.fail("Unable to set up details.");
        }

        // this should throw
        getGridElement().getDetails(100);
    }

    @Test
    public void openingDetailsOutOfView() {
        getGridElement().scrollToRow(500);

        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);

        getGridElement().scrollToRow(0);

        // if this fails, it'll fail before the assertNotNull
        assertNotNull("unexpected null details row",
                getGridElement().getDetails(0));
    }

    @Test
    public void togglingAVisibleDetailsRowWithOneRoundtrip() {
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS); // open

        assertTrue("Unexpected generator content",
                getGridElement().getDetails(0).getText().endsWith("(0)"));
        selectMenuPath(TOGGLE_FIRST_ITEM_DETAILS);
        assertTrue("New component was not displayed in the client",
                getGridElement().getDetails(0).getText().endsWith("(1)"));
    }

    @Test
    public void almostLastItemIdIsRendered() {
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        selectMenuPath(OPEN_ALMOST_LAST_ITEM_DETAILS);
        scrollGridVerticallyTo(100000);

        TestBenchElement details = getGridElement()
                .getDetails(ALMOST_LAST_INDEX);
        assertNotNull(details);
        assertTrue("Unexpected details content",
                details.getText().endsWith(ALMOST_LAST_INDEX + " (0)"));
    }

    @Test
    public void persistingChangesWorkInDetails() {
        selectMenuPath(DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        assertEquals("One", getGridElement().getDetails(0).getText());
        selectMenuPath(CHANGE_HIERARCHY);
        assertEquals("Two", getGridElement().getDetails(0).getText());
    }

    @Test
    public void persistingChangesWorkInDetailsWhileOutOfView() {
        selectMenuPath(DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        assertEquals("One", getGridElement().getDetails(0).getText());
        scrollGridVerticallyTo(10000);
        selectMenuPath(CHANGE_HIERARCHY);
        scrollGridVerticallyTo(0);
        assertEquals("Two", getGridElement().getDetails(0).getText());
    }

    @Test
    public void persistingChangesWorkInDetailsWhenNotAttached() {
        selectMenuPath(DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        assertEquals("One", getGridElement().getDetails(0).getText());

        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        assertFalse("Details should be detached",
                getGridElement().isElementPresent(By.vaadin("#details[0]")));

        selectMenuPath(CHANGE_HIERARCHY);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);

        assertEquals("Two", getGridElement().getDetails(0).getText());
    }

    @Test
    public void swappingDetailsGenerators_noDetailsShown() {
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        selectMenuPath(DETAILS_GENERATOR_NULL);
        assertFalse("Got some errors", $(NotificationElement.class).exists());
    }

    @Test
    public void swappingDetailsGenerators_shownDetails() {
        selectMenuPath(DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        assertTrue("Details should contain 'One' at first",
                getGridElement().getDetails(0).getText().contains("One"));

        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        assertFalse(
                "Details should contain 'Watching' after swapping generator",
                getGridElement().getDetails(0).getText().contains("Watching"));
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showNever() {
        scrollGridVerticallyTo(1000);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        assertFalse("Got some errors", $(NotificationElement.class).exists());
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showAfter() {
        scrollGridVerticallyTo(1000);
        selectMenuPath(DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        scrollGridVerticallyTo(0);

        assertFalse("Got some errors", $(NotificationElement.class).exists());
        assertNotNull("Could not find a details",
                getGridElement().getDetails(0));
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showBefore() {
        selectMenuPath(DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        scrollGridVerticallyTo(1000);

        assertFalse("Got some errors", $(NotificationElement.class).exists());
        assertNotNull("Could not find a details",
                getGridElement().getDetails(0));
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showBeforeAndAfter() {
        selectMenuPath(DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        scrollGridVerticallyTo(1000);
        scrollGridVerticallyTo(0);

        assertFalse("Got some errors", $(NotificationElement.class).exists());
        assertNotNull("Could not find a details",
                getGridElement().getDetails(0));
    }

    @Test
    public void noAssertErrorsOnEmptyDetailsAndScrollDown() {
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        scrollGridVerticallyTo(500);
        assertFalse(logContainsText("AssertionError"));
    }

    @Test
    public void noAssertErrorsOnPopulatedDetailsAndScrollDown() {
        selectMenuPath(DETAILS_GENERATOR_WATCHING);
        selectMenuPath(OPEN_FIRST_ITEM_DETAILS);
        scrollGridVerticallyTo(500);
        assertFalse(logContainsText("AssertionError"));
    }

}
