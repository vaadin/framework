package com.vaadin.tests.layouts.gridlayout;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class GridLayoutCaptionOnBottomAlignedComponentTest extends MultiBrowserTest {

    @Test
    public void captionShouldBeImmediatelyAboveItsComponent() {
        openTestURL();
        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();
        WebElement caption = gridLayout.findElement(By.className("v-caption"));
        TextFieldElement component = $(TextFieldElement.class).first();

        assertEquals("Caption and component have the same horizontal alignment",
            caption.getLocation().x, component.getLocation().x);

        // We have to do the assertion in this way because different browsers on different operating systems
        // measure the height of the caption in different ways.
        int diff = Math.abs(caption.getLocation().y - component.getLocation().y + caption.getSize().height);
        assertLessThanOrEqual("Caption is placed directly above the component", diff, 1);
    }

    @Test
    public void captionShouldStillBeImmediatelyAboveItsComponentEvenWhenRealigned() {
        openTestURL();
        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();
        WebElement caption = gridLayout.findElement(By.className("v-caption"));
        TextFieldElement component = $(TextFieldElement.class).first();

        // Click the button, this changes the alignment of the component
        $(ButtonElement.class).first().click();

        assertEquals("Caption and component have the same horizontal alignment",
            caption.getLocation().x, component.getLocation().x);

        assertEquals("Caption is placed in the top-left corner",
            gridLayout.getLocation().y, caption.getLocation().y);
    }
}
