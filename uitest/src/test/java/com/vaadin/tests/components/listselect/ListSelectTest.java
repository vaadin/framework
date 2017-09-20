package com.vaadin.tests.components.listselect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.elements.AbstractComponentElement.ReadOnlyException;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class ListSelectTest extends SingleBrowserTestPhantomJS2 {
    @Before
    public void setUp() throws Exception {
        openTestURL();
    }

    @Test
    public void initialLoad_containsCorrectItems() {
        assertItems(20);
    }

    @Test
    public void initialItems_reduceItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        assertItems(5);
    }

    @Test
    public void initialItems_increaseItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "100");
        assertItems(100);
    }

    @Test
    public void clickToSelect() {
        selectMenuPath("Component", "Listeners", "Selection listener");

        selectItem("Item 4");
        Assert.assertEquals("1. Selected: [Item 4]", getLogRow(0));

        selectItem("Item 2");
        Assert.assertEquals("3. Selected: [Item 2]", getLogRow(0));

        addItemsToSelection("Item 4");
        Assert.assertEquals("4. Selected: [Item 2, Item 4]", getLogRow(0));

        addItemsToSelection("Item 10", "Item 0", "Item 9"); // will cause 3
                                                            // events

        Assert.assertEquals(
                "7. Selected: [Item 2, Item 4, Item 10, Item 0, Item 9]",
                getLogRow(0));

        removeItemsFromSelection("Item 0", "Item 2", "Item 9"); // will cause 3
                                                                // events
        Assert.assertEquals("10. Selected: [Item 4, Item 10]", getLogRow(0));
    }

    @Test
    public void keyboardSelect() {
        selectMenuPath("Component", "Listeners", "Selection listener");

        selectItem("Item 4");
        Assert.assertEquals("1. Selected: [Item 4]", getLogRow(0));

        getListSelect().findElement(By.tagName("select")).sendKeys(Keys.ARROW_UP);

        Assert.assertEquals("2. Selected: [Item 3]", getLogRow(0));

        getListSelect().findElement(By.tagName("select")).sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN);

        Assert.assertEquals("4. Selected: [Item 5]", getLogRow(0));

    }

    @Test
    public void disabled_clickToSelect() {
        selectMenuPath("Component", "State", "Enabled");

        List<WebElement> select = getListSelect()
                .findElements(By.tagName("select"));
        Assert.assertEquals(1, select.size());
        Assert.assertNotNull(select.get(0).getAttribute("disabled"));

        selectMenuPath("Component", "Listeners", "Selection listener");

        String lastLogRow = getLogRow(0);

        selectItem("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
        assertNothingSelected();

        addItemsToSelection("Item 2");
        Assert.assertEquals(lastLogRow, getLogRow(0));
        assertNothingSelected();

        removeItemsFromSelection("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
        assertNothingSelected();
    }

    @Test
    public void readOnly_clickToSelect() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectMenuPath("Component", "State", "Readonly");

        List<WebElement> select = getListSelect()
                .findElements(By.tagName("select"));
        Assert.assertEquals(1, select.size());
        Assert.assertNotNull(select.get(0).getAttribute("disabled"));

        String lastLogRow = getLogRow(0);

        selectItem("Item 4");
        Assert.assertEquals(lastLogRow, getLogRow(0));
        assertNothingSelected();
    }

    @Test(expected = ReadOnlyException.class)
    public void readOnly_selectByText() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectMenuPath("Component", "State", "Readonly");

        List<WebElement> select = getListSelect()
                .findElements(By.tagName("select"));
        Assert.assertEquals(1, select.size());
        Assert.assertNotNull(select.get(0).getAttribute("disabled"));

        addItemsToSelection("Item 2");
    }

    @Test(expected = ReadOnlyException.class)
    public void readOnly_deselectByText() {
        selectMenuPath("Component", "Listeners", "Selection listener");

        selectItem("Item 4");

        selectMenuPath("Component", "State", "Readonly");

        List<WebElement> select = getListSelect()
                .findElements(By.tagName("select"));
        Assert.assertEquals(1, select.size());
        Assert.assertNotNull(select.get(0).getAttribute("disabled"));

        removeItemsFromSelection("Item 4");
    }

    @Test
    public void clickToSelect_reenable() {
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Listeners", "Selection listener");

        selectItem("Item 4");
        assertNothingSelected();

        selectMenuPath("Component", "State", "Enabled");

        selectItem("Item 5");
        Assert.assertEquals("3. Selected: [Item 5]", getLogRow(0));

        selectItem("Item 1");
        Assert.assertEquals("5. Selected: [Item 1]", getLogRow(0));

        addItemsToSelection("Item 2");
        Assert.assertEquals("6. Selected: [Item 1, Item 2]", getLogRow(0));

        removeItemsFromSelection("Item 1");
        Assert.assertEquals("7. Selected: [Item 2]", getLogRow(0));
    }

    @Test
    public void clickToSelect_notReadOnly() {
        selectMenuPath("Component", "State", "Readonly");
        selectMenuPath("Component", "Listeners", "Selection listener");

        selectItem("Item 4");
        assertNothingSelected();

        selectMenuPath("Component", "State", "Readonly");

        selectItem("Item 5");
        Assert.assertEquals("3. Selected: [Item 5]", getLogRow(0));

        selectItem("Item 1");
        Assert.assertEquals("5. Selected: [Item 1]", getLogRow(0));

        addItemsToSelection("Item 2");
        Assert.assertEquals("6. Selected: [Item 1, Item 2]", getLogRow(0));

        removeItemsFromSelection("Item 1");
        Assert.assertEquals("7. Selected: [Item 2]", getLogRow(0));
    }

    @Test
    public void itemCaptionProvider() {
        selectMenuPath("Component", "Item Generator", "Item Caption Generator",
                "Custom Caption Generator");
        assertItems(20, " Caption");
    }

    @Test
    public void selectProgramatically() {
        selectMenuPath("Component", "Listeners", "Selection listener");

        selectMenuPath("Component", "Selection", "Toggle Item 5");
        Assert.assertEquals("2. Selected: [Item 5]", getLogRow(0));
        assertSelected("Item 5");

        selectMenuPath("Component", "Selection", "Toggle Item 1");
        // Selection order (most recently selected is last)
        Assert.assertEquals("4. Selected: [Item 5, Item 1]", getLogRow(0));
        // DOM order
        assertSelected("Item 1", "Item 5");

        selectMenuPath("Component", "Selection", "Toggle Item 5");
        Assert.assertEquals("6. Selected: [Item 1]", getLogRow(0));
        assertSelected("Item 1");
    }

    private List<String> getSelectedValues() {
        Select select = new Select(
                getListSelect().findElement(By.tagName("select")));
        return select.getAllSelectedOptions().stream().map(e -> e.getText())
                .collect(Collectors.toList());
    }

    private void assertSelected(String... expectedSelection) {
        Assert.assertEquals(Arrays.asList(expectedSelection),
                getSelectedValues());
    }

    @Override
    protected Class<?> getUIClass() {
        return ListSelectTestUI.class;
    }

    protected ListSelectElement getListSelect() {
        return $(ListSelectElement.class).first();
    }

    protected void selectItem(String text) {
        // phantomjs1 seems to be adding to selection when clicked items, thus
        // need to deselect all clicking, which makes this test kind of
        // nothing...
        Select select = new Select(
                getListSelect().findElement(By.tagName("select")));
        select.deselectAll();

        Optional<WebElement> first = select.getOptions().stream()
                .filter(element -> text.equals(element.getText())).findFirst();
        if (first.isPresent()) {
            first.get().click();
        } else {
            Assert.fail("No element present with text " + text);
        }
    }

    protected void addItemsToSelection(String... items) {
        // acts as multi selection, no need to press modifier key
        Stream.of(items).forEach(text -> getListSelect().selectByText(text));
    }

    protected void removeItemsFromSelection(String... items) {
        Stream.of(items).forEach(text -> getListSelect().deselectByText(text));
    }

    protected void assertItems(int count) {
        assertItems(count, "");
    }

    private void assertNothingSelected() {
        Assert.assertEquals(0, getSelectedValues().size());
    }

    protected void assertItems(int count, String suffix) {
        int i = 0;
        for (String text : getListSelect().getOptions()) {
            assertEquals("Item " + i + suffix, text);
            i++;
        }
        assertEquals("Number of items", count, i);
    }

    protected void assertItemSuffices(int count) {
        int i = 0;
        for (String text : getListSelect().getOptions()) {
            assertTrue(text.endsWith("Item " + i));
            i++;
        }
        assertEquals("Number of items", count, i);
    }

}
