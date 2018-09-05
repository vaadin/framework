package com.vaadin.tests.components.radiobuttongroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.components.radiobutton.RadioButtonGroupTestUI;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for RadioButtonGroup
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class RadioButtonGroupTest extends MultiBrowserTest {

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
    public void disabled_reduceItemCount_containsCorrectItems() {
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Data provider", "Items", "5");
        assertItems(5);
    }

    @Test
    public void initialItems_increaseItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "100");
        assertItems(100);
    }

    @Test
    public void disabled_increaseItemCountWithinPushRows_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Data provider", "Items", "20");
        assertItems(20);
    }

    @Test
    public void disabled_increaseItemCountBeyondPushRows_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Data provider", "Items", "100");
        assertItems(100);
    }

    @Test
    public void clickToSelect() {
        selectMenuPath("Component", "Listeners", "Selection listener");

        getSelect().selectByText("Item 4");
        assertEquals("1. Selected: Optional[Item 4]", getLogRow(0));

        getSelect().selectByText("Item 2");
        assertEquals("2. Selected: Optional[Item 2]", getLogRow(0));

        getSelect().selectByText("Item 4");
        assertEquals("3. Selected: Optional[Item 4]", getLogRow(0));
    }

    @Test
    public void disabled_clickToSelect() {
        selectMenuPath("Component", "State", "Enabled");

        assertTrue(getSelect().findElements(By.tagName("input")).stream()
                .allMatch(element -> element.getAttribute("disabled") != null));

        selectMenuPath("Component", "Listeners", "Selection listener");

        String lastLogRow = getLogRow(0);

        getSelect().selectByText("Item 4");
        assertEquals(lastLogRow, getLogRow(0));

        getSelect().selectByText("Item 2");
        assertEquals(lastLogRow, getLogRow(0));

        getSelect().selectByText("Item 4");
        assertEquals(lastLogRow, getLogRow(0));
    }

    @Test // #9258
    public void disabled_correctClassNamesApplied() {
        openTestURL("theme=valo");
        selectMenuPath("Component", "State", "Enabled");

        List<WebElement> options = getSelect().findElements(By.tagName("span"));
        assertTrue(options.size() > 0);
        options.stream().map(element -> element.getAttribute("className"))
                .forEach(className -> verifyRadioButtonDisabledClassNames(
                        className, true));

        selectMenuPath("Component", "State", "Enabled");

        options = getSelect().findElements(By.tagName("span"));
        assertTrue(options.size() > 0);
        options.stream().map(element -> element.getAttribute("className"))
                .forEach(className -> verifyRadioButtonDisabledClassNames(
                        className, false));
    }

    @Test // #9258
    public void itemDisabledWithEnabledProvider_correctClassNamesApplied() {
        openTestURL("theme=valo");

        List<WebElement> options = getSelect().findElements(By.tagName("span"));

        assertTrue(options.size() > 0);
        options.stream().map(element -> element.getAttribute("className"))
                .forEach(cs -> verifyRadioButtonDisabledClassNames(cs, false));

        selectMenuPath("Component", "Item Enabled Provider",
                "Item Enabled Provider", "Disable Item 0");

        String className = getSelect().findElements(By.tagName("span")).get(0)
                .getAttribute("className");
        verifyRadioButtonDisabledClassNames(className, true);

        selectMenuPath("Component", "Item Enabled Provider",
                "Item Enabled Provider", "Disable Item 3");

        className = getSelect().findElements(By.tagName("span")).get(0)
                .getAttribute("className");
        verifyRadioButtonDisabledClassNames(className, false);

        className = getSelect().findElements(By.tagName("span")).get(3)
                .getAttribute("className");
        verifyRadioButtonDisabledClassNames(className, true);

        selectMenuPath("Component", "State", "Enabled");

        options = getSelect().findElements(By.tagName("span"));

        assertTrue(options.size() > 0);
        options.stream().map(element -> element.getAttribute("className"))
                .forEach(cs -> verifyRadioButtonDisabledClassNames(cs, true));

        selectMenuPath("Component", "Item Enabled Provider",
                "Item Enabled Provider", "Disable Item 5");

        options = getSelect().findElements(By.tagName("span"));

        assertTrue(options.size() > 0);
        options.stream().map(element -> element.getAttribute("className"))
                .forEach(cs -> verifyRadioButtonDisabledClassNames(cs, true));

        selectMenuPath("Component", "State", "Enabled");

        options = getSelect().findElements(By.tagName("span"));
        className = options.remove(5).getAttribute("className");

        assertTrue(options.size() > 0);
        options.stream().map(element -> element.getAttribute("className"))
                .forEach(cs -> verifyRadioButtonDisabledClassNames(cs, false));
        verifyRadioButtonDisabledClassNames(className, true);
    }

    @Test // #3387
    public void shouldApplySelectedClassToSelectedItems() {
        openTestURL("theme=valo");
        selectMenuPath("Component", "Selection", "Toggle Item 5");

        String className = getSelect().findElements(By.tagName("span")).get(5)
                .getAttribute("className");
        assertTrue("No v-select-option-selected class, was " + className,
                className.contains("v-select-option-selected"));

        getSelect().selectByText("Item 5");
        className = getSelect().findElements(By.tagName("span")).get(5)
                .getAttribute("className");
        assertTrue("No v-select-option-selected class, was " + className,
                className.contains("v-select-option-selected"));

        getSelect().selectByText("Item 10");
        List<WebElement> options = getSelect().findElements(By.tagName("span"));
        className = options.get(5).getAttribute("className");
        assertFalse("Extra v-select-option-selected class, was " + className,
                className.contains("v-select-option-selected"));
        className = options.get(10).getAttribute("className");
        assertTrue("No v-select-option-selected class, was " + className,
                className.contains("v-select-option-selected"));

        selectMenuPath("Component", "Selection", "Toggle Item 10");
        className = getSelect().findElements(By.tagName("span")).get(10)
                .getAttribute("className");
        assertFalse("Extra v-select-option-selected class, was " + className,
                className.contains("v-select-option-selected"));
    }

    @Test
    public void itemIconGenerator() {
        selectMenuPath("Component", "Item Icon Generator",
                "Use Item Icon Generator");
        assertItemsSuffices(20);

        List<WebElement> icons = getSelect()
                .findElements(By.cssSelector(".v-select-optiongroup .v-icon"));

        assertEquals(20, icons.size());

        for (int i = 0; i < icons.size(); i++) {
            assertEquals(VaadinIcons.values()[i + 1].getCodepoint(),
                    icons.get(i).getText().charAt(0));
        }
    }

    @Test
    public void clickToSelect_reenable() {
        selectMenuPath("Component", "State", "Enabled");
        selectMenuPath("Component", "Listeners", "Selection listener");

        getSelect().selectByText("Item 4");

        selectMenuPath("Component", "State", "Enabled");

        getSelect().selectByText("Item 5");
        assertEquals("3. Selected: Optional[Item 5]", getLogRow(0));

        getSelect().selectByText("Item 2");
        assertEquals("4. Selected: Optional[Item 2]", getLogRow(0));

        getSelect().selectByText("Item 4");
        assertEquals("5. Selected: Optional[Item 4]", getLogRow(0));
    }

    @Test
    public void itemCaptionGenerator() {
        selectMenuPath("Component", "Item Caption Generator",
                "Item Caption Generator", "Custom Caption Generator");
        assertItems(20, " Caption");
    }

    @Test
    public void nullItemCaptionGenerator() {
        selectMenuPath("Component", "Item Caption Generator",
                "Item Caption Generator", "Null Caption Generator");
        for (String text : getSelect().getOptions()) {
            assertEquals("", text);
        }
    }

    @Test
    public void selectProgramatically() {
        selectMenuPath("Component", "Listeners", "Selection listener");

        selectMenuPath("Component", "Selection", "Toggle Item 5");
        assertEquals("2. Selected: Optional[Item 5]", getLogRow(0));
        assertSelected("Item 5");

        selectMenuPath("Component", "Selection", "Toggle Item 1");
        assertEquals("4. Selected: Optional[Item 1]", getLogRow(0));
        // DOM order
        assertSelected("Item 1");

        selectMenuPath("Component", "Selection", "Toggle Item 5");
        assertEquals("6. Selected: Optional[Item 5]", getLogRow(0));
        assertSelected("Item 5");
    }

    @Test
    public void testItemDescriptionGenerators() {
        TestBenchElement label;

        selectMenuPath("Component", "Item Description Generator",
                "Item Description Generator", "Default Description Generator");

        label = (TestBenchElement) findElements(By.tagName("label")).get(5);
        label.showTooltip();
        assertEquals("Tooltip should contain the same text as caption",
                label.getText(), getTooltipElement().getText());

        selectMenuPath("Component", "Item Description Generator",
                "Item Description Generator", "Custom Description Generator");

        label = (TestBenchElement) findElements(By.tagName("label")).get(5);
        label.showTooltip();
        assertEquals("Tooltip should contain caption + ' Description'",
                label.getText() + " Description",
                getTooltipElement().getText());
    }

    private void assertSelected(String expectedSelection) {
        assertEquals(expectedSelection, getSelect().getValue());
    }

    @Override
    protected Class<?> getUIClass() {
        return RadioButtonGroupTestUI.class;
    }

    protected RadioButtonGroupElement getSelect() {
        return $(RadioButtonGroupElement.class).first();
    }

    protected void assertItems(int count) {
        assertItems(count, "");
    }

    protected void assertItems(int count, String suffix) {
        int i = 0;
        for (String text : getSelect().getOptions()) {
            assertEquals("Item " + i + suffix, text);
            i++;
        }
        assertEquals("Number of items", count, i);
    }

    protected void assertItemsSuffices(int count) {
        int i = 0;
        for (String text : getSelect().getOptions()) {
            assertTrue(text.endsWith("Item " + i));
            i++;
        }
        assertEquals("Number of items", count, i);
    }

    // needed to make tooltips work in IE tests
    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    private static void verifyRadioButtonDisabledClassNames(String className,
            boolean disabled) {
        assertEquals(
                disabled ? "No"
                        : "Extra" + " v-radiobutton-disabled class, was "
                                + className,
                disabled, className.contains("v-radiobutton-disabled"));
        assertEquals(
                disabled ? "No"
                        : "Extra" + " v-disabled class, was " + className,
                disabled, className.contains("v-disabled"));
    }
}
