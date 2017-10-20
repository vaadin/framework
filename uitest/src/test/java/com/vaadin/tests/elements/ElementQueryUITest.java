package com.vaadin.tests.elements;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ElementQueryUITest extends MultiBrowserTest {

    @Test
    public void firstGetAllLast() {
        openTestURL();
        assertEquals("Button 0", $(ButtonElement.class).first().getCaption());
        assertEquals("Button 9", $(ButtonElement.class).last().getCaption());

        List<ButtonElement> all = $(ButtonElement.class).all();

        for (int i = 0; i < 10; i++) {
            assertEquals("Button " + i, all.get(i).getCaption());
            assertEquals("Button " + i,
                    $(ButtonElement.class).get(i).getCaption());
        }
    }
}
