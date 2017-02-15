package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ComboBoxBackEndRequestsTest extends SingleBrowserTest {

    @Before
    public void open() {
        openTestURL("?debug");
    }

    @Test
    public void testInitialLoad_onlySizeAndFirstItemsRequested() {
        verifyInitialLoadRequests();
    }

    @Test
    public void testOpeningDropDown_noRequests() {
        verifyInitialLoadRequests();

        clearLogs();
        openPopup();

        verifyNumberOrRequests(
                "opening drop down should not have caused requests", 0);

        // select something to close popup
        selectByClick("Item 2");

        verifyNumberOrRequests("selecting should have not caused requests", 0);

        openPopup();
        verifyNumberOrRequests(
                "opening drop down should not have caused requests", 0);
    }

    @Test
    public void testPagingWorks_nullSelectionAllowed_defaultSizes() {
        verifyPopupPagesForDefaultSizes(
                ComboBoxBackEndRequests.DEFAULT_PAGE_LENGTH,
                ComboBoxBackEndRequests.DEFAULT_NUMBER_OF_ITEMS, true);
    }

    @Test
    public void testPagingWorks_nullSeletionDisallowed_defaultSizes() {
        triggerNullSelectionAllowed(false);

        verifyPopupPagesForDefaultSizes(
                ComboBoxBackEndRequests.DEFAULT_PAGE_LENGTH,
                ComboBoxBackEndRequests.DEFAULT_NUMBER_OF_ITEMS, false);
    }

    @Test
    public void testPagingWorks_nullSelectionAllowed_customSizes() {
        final int pageLength = 23;
        final int items = 333;
        openTestURL(ComboBoxBackEndRequests.PAGE_LENGTH_REQUEST_PARAMETER + "="
                + pageLength + "&"
                + ComboBoxBackEndRequests.ITEMS_REQUEST_PARAMETER + "=" + items
                + "&restartApplication");

        // with null selection allowed
        verifyPopupPagesForDefaultSizes(pageLength, items, true);
    }

    @Test
    public void testPagingWorks_nullSelectionDisallowed_customSizes() {
        final int pageLength = 23;
        final int items = 333;
        openTestURL(ComboBoxBackEndRequests.PAGE_LENGTH_REQUEST_PARAMETER + "="
                + pageLength + "&"
                + ComboBoxBackEndRequests.ITEMS_REQUEST_PARAMETER + "=" + items
                + "&restartApplication");

        triggerNullSelectionAllowed(false);

        verifyPopupPagesForDefaultSizes(pageLength, items, false);
    }

    // @Test TODO cache strategy is still broken for CB
    public void testPaging_usesCachedData() {
        verifyInitialLoadRequests();
        clearLogs();
        openPopup();
        verifyNumberOrRequests(
                "opening drop down should not have caused requests", 0);

        nextPage();

        verifyNumberOrRequests("next page should have not caused more requests",
                0);
    }

    @Test
    public void testPaging_nullSelectionAllowed_correctNumberOfItemsShown() {
        verifyInitialLoadRequests();
        openPopup();

        verifyPopupItems(true, 0, 8);

        nextPage();

        verifyPopupItems(false, 9, 18);
    }

    @Test
    public void testPaging_nullSelectionDisallowed_correctNumberOfItemsShown() {
        verifyInitialLoadRequests();
        triggerNullSelectionAllowed(false);

        openPopup();

        verifyPopupItems(false, 0, 9);

        nextPage();

        verifyPopupItems(false, 10, 19);
    }

    private void triggerNullSelectionAllowed(boolean expectedValue) {
        $(CheckBoxElement.class).caption("emptySelectionAllowed").first()
                .click();
        assertEquals(expectedValue, $(CheckBoxElement.class)
                .caption("emptySelectionAllowed").first().isChecked());
    }

    private void clearLogs() {
        $(ButtonElement.class).caption("Clear logs").first().click();
        verifyNumberOrRequests("logs should have been cleared", 0);
    }

    private void selectByClick(String itemText) {
        $(ComboBoxElement.class).first().getPopupSuggestionElements().stream()
                .filter(element -> element.getText().equals(itemText))
                .findFirst().get().click();
        assertEquals("Wrong selected", itemText,
                $(ComboBoxElement.class).first().getValue());
    }

    private void nextPage() {
        assertTrue("no next page available",
                $(ComboBoxElement.class).first().openNextPage());
    }

    private void previousPage() {
        assertTrue("no previous page available",
                $(ComboBoxElement.class).first().openPrevPage());
    }

    private void openPopup() {
        ComboBoxElement element = $(ComboBoxElement.class).first();
        assertFalse("popup shouldn't be open", element.isPopupOpen());

        element.getSuggestionPopup();

        assertTrue("popup should be open", element.isPopupOpen());
    }

    private void verifyPopupPagesForDefaultSizes(int pageSize, int totalItems,
            boolean nullSelectionAllowed) {
        int numberOfPages = new BigDecimal(
                totalItems + (nullSelectionAllowed ? 1 : 0))
                        .divide(new BigDecimal(pageSize), RoundingMode.DOWN)
                        .intValue();
        final int lastPageSize = (totalItems + (nullSelectionAllowed ? 1 : 0))
                % pageSize;
        if (lastPageSize > 0) {
            numberOfPages++;
        }
        openPopup();

        verifyPopupStatus(pageSize, 1, numberOfPages, totalItems,
                nullSelectionAllowed);

        for (int i = 2; i <= numberOfPages; i++) {
            nextPage();
            verifyPopupStatus(pageSize, i, numberOfPages, totalItems,
                    nullSelectionAllowed);
        }

        assertFalse("there should not be a next page",
                $(ComboBoxElement.class).first().openNextPage());

        if (numberOfPages < 2) {
            return;
        }
        for (int i = numberOfPages - 1; i > 0; i--) {
            previousPage();
            verifyPopupStatus(pageSize, i, numberOfPages, totalItems,
                    nullSelectionAllowed);
        }

        assertFalse("there should not be a previous page",
                $(ComboBoxElement.class).first().openPrevPage());
    }

    private void verifyPopupStatus(int pageSize, int currentPage,
            int numberOfPages, int totalItems, boolean nullSelectionAllowed) {
        int start;
        int end;

        if (currentPage == 1) { // first page
            start = 1;
            end = pageSize - (nullSelectionAllowed ? 1 : 0);
        } else if (currentPage == numberOfPages) { // last page
            final int lastPageSize = (totalItems
                    + (nullSelectionAllowed ? 1 : 0)) % pageSize;

            start = pageSize * (currentPage - 1)
                    + (nullSelectionAllowed ? 0 : 1);
            end = start + (lastPageSize == 0 ? pageSize : lastPageSize) - 1;
        } else { // all other
            start = pageSize * (currentPage - 1)
                    + (nullSelectionAllowed ? 0 : 1);
            end = pageSize * currentPage - (nullSelectionAllowed ? 1 : 0);
        }

        WebElement element = findElement(By.className("v-filterselect-status"));
        assertEquals(
                "Wrong status text on popup page " + currentPage + "/"
                        + numberOfPages,
                start + "-" + end + "/" + totalItems, element.getText());
    }

    private void verifyPopupItems(boolean hasNullSelectionItem, int startIndex,
            int lastIndex) {
        List<String> popupSuggestions = $(ComboBoxElement.class).first()
                .getPopupSuggestions();
        if (hasNullSelectionItem) {
            String text = popupSuggestions.remove(0);
            assertEquals("nullSelectionItem should be visible on page", " ",
                    text);
        }
        assertEquals("invalid number of suggestions",
                lastIndex - startIndex + 1, popupSuggestions.size());
        for (int i = startIndex; i <= lastIndex; i++) {
            assertEquals("unexpected item", "Item " + i,
                    popupSuggestions.get(i - startIndex));
        }
    }

    private void verifyInitialLoadRequests() {
        verifyNumberOrRequests("two initial requests expected", 2);
        verifySizeRequest(0, null);
        verifyFetchRequest(1, 0, 40, null);
    }

    private WebElement verifyFetchRequest(int requestNumber, int startIndex,
            int size, String filter) {
        WebElement element = verifyRequest(requestNumber, "FETCH");
        // format is 1: FETCH 0 40 _filter_
        final String actual = element.getText();
        String expected = requestNumber + ": FETCH " + startIndex + " " + size
                + (filter == null ? "" : " " + filter);
        assertEquals("invalid fetch data", expected, actual);
        return element;

    }

    private WebElement verifySizeRequest(int index, String filter) {
        WebElement element = verifyRequest(index, "SIZE");
        if (filter != null) {
            // format is "0: SIZE 0 2147483647 _filter_
            String currentFilter = element.getText().split("2147483647")[1];
            assertEquals("Wrong filter", filter, currentFilter);
        }
        return element;
    }

    private WebElement verifyRequest(int index, String queryType) {
        WebElement element = findElement(By.id(queryType + "-" + index));
        return element;
    }

    private void verifyNumberOrRequests(String message, int numberOfRequests) {
        List<WebElement> logRows = getLogRows();
        assertEquals(message, numberOfRequests, logRows.size());
    }

    private List<WebElement> getLogRows() {
        return findElements(By.className("log"));
    }
}
