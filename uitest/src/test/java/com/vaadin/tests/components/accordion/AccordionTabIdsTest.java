package com.vaadin.tests.components.accordion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for Accordion: Tab.setId should be propagated to client side tabs.
 *
 * @author Vaadin Ltd
 */
public class AccordionTabIdsTest extends MultiBrowserTest {

    @Test
    public void testGeTabByIds() {
        openTestURL();
        ButtonElement setIdButton = $(ButtonElement.class).first();
        ButtonElement clearIdbutton = $(ButtonElement.class).get(1);

        WebElement firstItem = driver
                .findElement(By.id(AccordionTabIds.FIRST_TAB_ID));
        WebElement label = $(LabelElement.class).context(firstItem).first();
        assertEquals(AccordionTabIds.FIRST_TAB_MESSAGE, label.getText());

        clearIdbutton.click();
        assertEquals("", firstItem.getAttribute("id"));

        setIdButton.click();
        assertEquals(AccordionTabIds.FIRST_TAB_ID,
                firstItem.getAttribute("id"));
    }
}
