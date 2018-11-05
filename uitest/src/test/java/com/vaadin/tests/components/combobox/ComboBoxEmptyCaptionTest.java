package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class ComboBoxEmptyCaptionTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void emptyItemCaption() {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        // empty string in caption becomes &nbsp; because of #7506
        ensureSuggestions(combo, " ", "item1", "item2", "item3", "item4",
                "item5", "item6", "item7", "item8", "item9");
    }

    @Test
    public void hasEmptyItemCaption() {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        // set some caption for the empty selection element
        $(ButtonElement.class).first().click();
        ensureSuggestions(combo, "empty", "item1", "item2", "item3", "item4",
                "item5", "item6", "item7", "item8", "item9");
    }

    @Test
    public void resetEmptyItem() {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        // set some caption for the empty selection element
        $(ButtonElement.class).first().click();
        // set empty string back as an empty caption
        $(ButtonElement.class).get(1).click();
        ensureSuggestions(combo, " ", "item1", "item2", "item3", "item4",
                "item5", "item6", "item7", "item8", "item9");
    }

    @Test
    public void disableEmptyItem() {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        // set some caption for the empty selection element
        $(ButtonElement.class).get(2).click();
        ensureSuggestions(combo, "item1", "item2", "item3", "item4", "item5",
                "item6", "item7", "item8", "item9", "item10");
    }

    @Test
    public void emptyItemCaptionInTextBox() {
        ComboBoxElement combo = $(ComboBoxElement.class).first();

        assertEquals("", combo.getInputField().getAttribute("value"));

        // set some caption for the empty selection element
        $(ButtonElement.class).first().click();

        assertEquals("empty", combo.getInputField().getAttribute("value"));

    }

    private void ensureSuggestions(ComboBoxElement element,
            String... suggestions) {
        element.openPopup();
        assertEquals(Arrays.asList(suggestions),
                new ArrayList<>(element.getPopupSuggestions()));
        // Close popup
        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();
    }

}
