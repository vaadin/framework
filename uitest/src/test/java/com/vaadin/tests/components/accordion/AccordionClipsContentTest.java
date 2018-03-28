package com.vaadin.tests.components.accordion;

import org.junit.Test;

import com.vaadin.testbench.elements.NativeButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class AccordionClipsContentTest extends MultiBrowserTest {
    @Override
    protected Class<?> getUIClass() {
        return AccordionTest.class;
    }

    @Test
    public void testAccordionClipsContent() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Component container features",
                "Add component", "NativeButton", "auto x auto");

        $(NativeButtonElement.class).first().click();

        compareScreen("button-clicked");
    }
}
