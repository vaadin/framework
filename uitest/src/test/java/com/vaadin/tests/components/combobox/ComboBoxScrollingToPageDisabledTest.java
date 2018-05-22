package com.vaadin.tests.components.combobox;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

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

    @Test
    public void checkUpdateFromServerDisplayedCorrectly() {
        ButtonElement selFirstButton = $(ButtonElement.class)
                .caption("Select first").first();
        ButtonElement sel50Button = $(ButtonElement.class)
                .caption("Select index 50").first();
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();

        selFirstButton.click();
        org.junit.Assert.assertEquals("Item 0", comboBox.getText());
        sel50Button.click();
        org.junit.Assert.assertEquals("Item 50", comboBox.getText());
        selFirstButton.click();
        org.junit.Assert.assertEquals("Item 0", comboBox.getText());
        sel50Button.click();
        org.junit.Assert.assertEquals("Item 50", comboBox.getText());
    }
}
