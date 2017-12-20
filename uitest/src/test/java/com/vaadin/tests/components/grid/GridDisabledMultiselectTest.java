package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridDisabledMultiselectTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    private void disable() {
        $(ButtonElement.class).caption("Disable").first().click();
    }

    private void setMultiselect() {
        $(ButtonElement.class).caption("Multi").first().click();
    }

    private WebElement getSelectAllCheckBox() {
        return findCheckBoxes().get(0);
    }

    private List<WebElement> findCheckBoxes() {
        return findElements(By.cssSelector("span input"));
    }

    private WebElement getFirstSelectCheckBox() {
        return findCheckBoxes().get(0);
    }

    @Test
    public void checkBoxesAreDisabledAfterModeChange() {
        disable();

        setMultiselect();

        assertFalse(getSelectAllCheckBox().isEnabled());
        assertFalse(getFirstSelectCheckBox().isEnabled());
    }

    @Test
    public void checkBoxesAreDisabledAfterDisabled() {
        setMultiselect();

        assertTrue(getSelectAllCheckBox().isEnabled());
        assertTrue(getFirstSelectCheckBox().isEnabled());

        disable();

        assertFalse(getSelectAllCheckBox().isEnabled());
        assertFalse(getFirstSelectCheckBox().isEnabled());
    }

    @Test
    public void parentSpanCannotBeClickedWhenDisabled() {
        setMultiselect();
        disable();

        WebElement firstCheckBoxSpan = findElements(By.cssSelector("span"))
                .get(1);
        new Actions(driver).moveToElement(firstCheckBoxSpan, 1, 1).click()
                .perform();

        assertFalse(getFirstSelectCheckBox().isSelected());
    }
}
