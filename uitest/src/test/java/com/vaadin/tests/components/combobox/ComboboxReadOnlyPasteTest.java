package com.vaadin.tests.components.combobox;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;

import static org.junit.Assert.assertFalse;

public class ComboboxReadOnlyPasteTest extends MultiBrowserTest {

    @Test
    public void popupNotOpened() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.click();
        String paste = Keys.chord(Keys.CONTROL, "v");
        cb.sendKeys(paste);
        assertFalse("Pop-up should not be opened in read-only mode",
                cb.isPopupOpen());
    }
}
