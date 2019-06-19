package com.vaadin.tests.components.ui;

import com.vaadin.testbench.elements.ButtonElement;
import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

import static org.junit.Assert.assertEquals;

public class RefreshUITest extends MultiBrowserTest {

    @Test
    public void testUIRefresh_viewNotRecreated() {
        openTestURL();
        assertEquals("The Label content is not matching",
                "This is instance no 1",
                $(LabelElement.class).first().getText());

        // Reload the page; UI.refresh should be invoked
        reloadPage();
        assertEquals("The Label content is not matching",
                "This is instance no 1",
                $(LabelElement.class).first().getText());
    }

    @Test
    public void testViewNotRecreatedAfterNavigation() {
        openTestURL();
        assertEquals("This is instance no 1",
                $(LabelElement.class).first().getText());

        // Reload the page; UI.refresh should be invoked
        reloadPage();
        assertEquals("This is instance no 1",
                $(LabelElement.class).first().getText());

        // now open the other view using the navigator
        $(ButtonElement.class).first().click();
        assertEquals("This is otherview instance no 1",
                $(LabelElement.class).first().getText());

        // Reload the page; UI.refresh should be invoked
        reloadPage();
        assertEquals("This is otherview instance no 1",
                $(LabelElement.class).first().getText());
    }
}
