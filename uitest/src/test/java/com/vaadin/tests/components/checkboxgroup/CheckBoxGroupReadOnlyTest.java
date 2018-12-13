package com.vaadin.tests.components.checkboxgroup;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class CheckBoxGroupReadOnlyTest extends MultiBrowserTest {

    @Test
    public void itemsAreReadOnly() {
        openTestURL();
        // Initially components are read-only
        assertTrue(getSelect().isReadOnly());
        assertEquals(4, findReadOnlyCheckboxes().size());
        // Should not contain v-readonly
        findElement(By.id("changeReadOnly")).click();
        assertEquals(0, findReadOnlyCheckboxes().size());

        // Should not contain v-readonly
        findElement(By.id("changeEnabled")).click();
        assertEquals(0, findReadOnlyCheckboxes().size());

        // make read-only
        findElement(By.id("changeReadOnly")).click();
        // enable
        findElement(By.id("changeEnabled")).click();
        // Should contain v-readonly
        assertEquals(4, findReadOnlyCheckboxes().size());
    }

    protected CheckBoxGroupElement getSelect() {
        return $(CheckBoxGroupElement.class).first();
    }

    private List<WebElement> findReadOnlyCheckboxes() {
        return findElement(By.id("cbg"))
                .findElements(By.cssSelector("span.v-readonly.v-checkbox"));
    }
}
