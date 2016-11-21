package com.vaadin.tests.components.grid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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

        assertThat(getSelectAllCheckBox().isEnabled(), is(false));
        assertThat(getFirstSelectCheckBox().isEnabled(), is(false));
    }

    @Test
    public void checkBoxesAreDisabledAfterDisabled() {
        setMultiselect();

        assertThat(getSelectAllCheckBox().isEnabled(), is(true));
        assertThat(getFirstSelectCheckBox().isEnabled(), is(true));

        disable();

        assertThat(getSelectAllCheckBox().isEnabled(), is(false));
        assertThat(getFirstSelectCheckBox().isEnabled(), is(false));
    }

    @Test
    public void parentSpanCannotBeClickedWhenDisabled() {
        setMultiselect();
        disable();

        WebElement firstCheckBoxSpan = findElements(By.cssSelector("span"))
                .get(1);
        new Actions(driver).moveToElement(firstCheckBoxSpan, 1, 1).click()
                .perform();

        assertThat(getFirstSelectCheckBox().isSelected(), is(false));
    }
}
