package com.vaadin.tests.components.combobox;

import org.junit.Test;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * When pressed down key, while positioned on the last item - should show next
 * page and focus on the first item of the next page.
 */
public class ComboBoxScrollingToPageDisabledTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void checkValueIsVisible() throws InterruptedException {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        org.junit.Assert.assertEquals("Item 50", combo.getText());
    }

    @Test
    public void checkLastValueIsVisible() throws InterruptedException {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        combo.selectByText("Item 99");
        // this shouldn't clear the selection
        combo.openPopup();
        // close popup
        $(LabelElement.class).first().click();

        org.junit.Assert.assertEquals("Item 99", combo.getText());
    }
}
