package com.vaadin.tests.components.grid;

import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridScrollDestinationTest extends SingleBrowserTest {

    private TextFieldElement textField;
    private ButtonElement button;
    private GridElement grid;
    private TestBenchElement header;
    private TestBenchElement tableWrapper;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        textField = $(TextFieldElement.class).first();
        button = $(ButtonElement.class).first();
        grid = $(GridElement.class).first();
        header = grid.getHeader();
        tableWrapper = grid.getTableWrapper();
    }

    private void assertElementAtTop(WebElement row) {
        assertThat((double) row.getLocation().getY(), closeTo(
                header.getLocation().getY() + header.getSize().getHeight(),
                1d));
    }

    private void assertElementAtBottom(WebElement row) {
        assertThat(
                (double) row.getLocation().getY() + row.getSize().getHeight(),
                closeTo((double) tableWrapper.getLocation().getY()
                        + tableWrapper.getSize().getHeight(), 1d));
    }

    private void assertElementAtMiddle(WebElement row) {
        assertThat((double) row.getLocation()
                .getY() + (row.getSize().getHeight() / 2), closeTo(
                        (double) tableWrapper.getLocation().getY()
                                + header.getSize().getHeight()
                                + ((tableWrapper.getSize().getHeight()
                                        - header.getSize().getHeight()) / 2),
                        1d));
    }

    @Test
    public void destinationAny() {
        // ScrollDestination.ANY selected by default

        // scroll down
        button.click();

        // expect the row at the bottom of the viewport
        List<WebElement> rows = grid.getBody()
                .findElements(By.className("v-grid-row"));
        // last rendered row is a buffer row, inspect second to last
        WebElement row = rows.get(rows.size() - 2);
        assertEquals("50", row.getText());

        assertElementAtBottom(row);

        // scroll to end
        grid.scrollToRow((int) grid.getRowCount() - 1);

        // ensure row 50 is out of visual range, first two rows are out of view
        // and getText can't find the contents so inspect the third row
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(2);

        assertGreater(row.getText() + " is not greater than 52",
                Integer.valueOf(row.getText()), 52);

        // scroll up
        button.click();

        // expect the row at the top of the viewport
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        // first rendered row is a buffer row, inspect second
        row = rows.get(1);
        assertEquals("50", row.getText());

        assertElementAtTop(row);

        // scroll up by a few rows
        grid.scrollToRow(45);

        // refresh row references
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(6);
        assertEquals("50", row.getText());

        // scroll while already within viewport
        button.click();

        // expect no change since the row is still within viewport
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(6);
        assertEquals("50", row.getText());

        // scroll to beginning using scroll destination
        textField.setValue("0");
        button.click();

        // expect to be scrolled all the way up
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(0);
        assertEquals("0", row.getText());

        assertElementAtTop(row);

        // scroll to end using scroll destination
        textField.setValue("99");
        button.click();

        // expect to be scrolled all the way down
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(rows.size() - 1);
        assertEquals("99", row.getText());

        assertElementAtBottom(row);
    }

    @Test
    public void destinationEnd() {
        $(NativeSelectElement.class).first()
                .selectByText(ScrollDestination.END.name());

        // scroll down
        button.click();

        // expect the row at the bottom of the viewport
        List<WebElement> rows = grid.getBody()
                .findElements(By.className("v-grid-row"));
        // last rendered row is a buffer row, inspect second to last
        WebElement row = rows.get(rows.size() - 2);
        assertEquals("50", row.getText());

        assertElementAtBottom(row);

        // scroll to end
        grid.scrollToRow((int) grid.getRowCount() - 1);

        // ensure row 50 is out of visual range, first two rows are out of view
        // and getText can't find the contents so inspect the third row
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(2);

        assertGreater(row.getText() + " is not greater than 52",
                Integer.valueOf(row.getText()), 52);

        // scroll up
        button.click();

        // expect the row at the bottom of the viewport
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        // last rendered row is a buffer row, inspect second to last
        row = rows.get(rows.size() - 2);
        assertEquals("50", row.getText());

        assertElementAtBottom(row);

        // scroll down by a few rows
        grid.scrollToRow(55);

        // refresh row references
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(rows.size() - 7);
        assertEquals("50", row.getText());

        // scroll while already within viewport
        button.click();

        // expect the row at the bottom of the viewport again
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(rows.size() - 2);
        assertEquals("50", row.getText());

        assertElementAtBottom(row);

        // scroll to beginning using scroll destination
        textField.setValue("0");
        button.click();
        button.click();

        // expect to be scrolled all the way up
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(0);
        assertEquals("0", row.getText());

        assertElementAtTop(row);

        // scroll to end using scroll destination
        textField.setValue("99");
        button.click();

        // expect to be scrolled all the way down
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(rows.size() - 1);
        assertEquals("99", row.getText());

        assertElementAtBottom(row);
    }

    @Test
    public void destinationStart() {
        $(NativeSelectElement.class).first()
                .selectByText(ScrollDestination.START.name());

        // scroll down
        button.click();

        // expect the row at the top of the viewport
        List<WebElement> rows = grid.getBody()
                .findElements(By.className("v-grid-row"));
        // first rendered row is a buffer row, inspect second
        WebElement row = rows.get(1);
        assertEquals("50", row.getText());

        assertElementAtTop(row);

        // scroll to end
        grid.scrollToRow((int) grid.getRowCount() - 1);

        // ensure row 50 is out of visual range, first two rows are out of view
        // and getText can't find the contents so inspect the third row
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(2);

        assertGreater(row.getText() + " is not greater than 52",
                Integer.valueOf(row.getText()), 52);

        // scroll up
        button.click();

        // expect the row at the top of the viewport
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        // first rendered row is a buffer row, inspect second
        row = rows.get(1);
        assertEquals("50", row.getText());

        assertElementAtTop(row);

        // scroll up by a few rows
        grid.scrollToRow(45);

        // refresh row references
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(6);
        assertEquals("50", row.getText());

        // scroll while already within viewport
        button.click();

        // expect the row at the top of the viewport again
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(1);
        assertEquals("50", row.getText());

        assertElementAtTop(row);

        // scroll to beginning using scroll destination
        textField.setValue("0");
        button.click();

        // expect to be scrolled all the way up
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(0);
        assertEquals("0", row.getText());

        assertElementAtTop(row);

        // scroll to end using scroll destination
        textField.setValue("99");
        button.click();

        // expect to be scrolled all the way down
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(rows.size() - 1);
        assertEquals("99", row.getText());

        assertElementAtBottom(row);
    }

    @Test
    public void destinationMiddle() {
        NativeSelectElement destinationSelect = $(NativeSelectElement.class)
                .first();
        destinationSelect.selectByText(ScrollDestination.MIDDLE.name());

        // scroll down
        button.click();

        // expect the row at the middle of the viewport
        List<WebElement> rows = grid.getBody()
                .findElements(By.className("v-grid-row"));
        // inspect the middle row
        WebElement row = rows.get(rows.size() / 2);
        assertEquals("50", row.getText());

        assertElementAtMiddle(row);

        // scroll to end
        grid.scrollToRow((int) grid.getRowCount() - 1);

        // ensure row 50 is out of visual range, first two rows are out of view
        // and getText can't find the contents so inspect the third row
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(2);

        assertGreater(row.getText() + " is not greater than 52",
                Integer.valueOf(row.getText()), 52);

        // scroll up
        button.click();

        // expect the row at the middle of the viewport
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        // first rendered row is a buffer row, inspect second
        row = rows.get(rows.size() / 2);
        assertEquals("50", row.getText());

        assertElementAtMiddle(row);

        // scroll down by a few rows
        destinationSelect.selectByText(ScrollDestination.START.name());
        button.click();

        // refresh row references
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(1);
        assertEquals("50", row.getText());

        // scroll while already within viewport
        destinationSelect.selectByText(ScrollDestination.MIDDLE.name());
        button.click();

        // expect the row at the top of the viewport again
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(rows.size() / 2);
        assertEquals("50", row.getText());

        assertElementAtMiddle(row);

        // scroll to beginning using scroll destination
        textField.setValue("0");
        button.click();

        // expect to be scrolled all the way up
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(0);
        assertEquals("0", row.getText());

        assertElementAtTop(row);

        // scroll to end using scroll destination
        textField.setValue("99");
        button.click();

        // expect to be scrolled all the way down
        rows = grid.getBody().findElements(By.className("v-grid-row"));
        row = rows.get(rows.size() - 1);
        assertEquals("99", row.getText());

        assertElementAtBottom(row);
    }
}
