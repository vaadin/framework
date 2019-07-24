package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxScrollToSelectedItemTest extends MultiBrowserTest {

    @Test
    public void initialOpeningShouldScrollToSelected() {
        openTestURL();

        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();

        WebElement selected = cb.getSuggestionPopup()
                .findElement(By.className("gwt-MenuItem-selected"));
        assertNotNull(selected);
        assertEquals("SHOW ME", selected.getText());
    }
}
