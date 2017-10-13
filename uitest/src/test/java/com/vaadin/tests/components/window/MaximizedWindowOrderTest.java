package com.vaadin.tests.components.window;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MaximizedWindowOrderTest extends MultiBrowserTest {

    private WindowElement openAnotherWindow() {
        WindowElement maximizedWindow = getMaximizedWindow();
        maximizedWindow.$(ButtonElement.class).first().click();

        return getAnotherWindow();
    }

    private WindowElement getMaximizedWindow() {
        return $(WindowElement.class).first();
    }

    private WindowElement getAnotherWindow() {
        return $(WindowElement.class).get(1);
    }

    private WindowElement openMaximizedWindow() {
        $(ButtonElement.class).first().click();

        return getMaximizedWindow();
    }

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void newWindowOpensOnTopOfMaximizedWindow() {
        WindowElement maximizedWindow = openMaximizedWindow();
        WindowElement anotherWindow = openAnotherWindow();

        assertTrue(anotherWindow.getCssValue("z-index")
                .compareTo(maximizedWindow.getCssValue("z-index")) > 0);

        assertEquals("10000", getMaximizedWindow().getCssValue("z-index"));
        assertEquals("10001", getAnotherWindow().getCssValue("z-index"));
    }

    @Test
    public void backgroundWindowIsBroughtOnTopWhenMaximized() {
        WindowElement maximizedWindow = openMaximizedWindow();

        maximizedWindow.restore();

        // the new window is opened on top of the original.
        WindowElement anotherWindow = openAnotherWindow();

        // move the window to make the maximize button visible.
        anotherWindow.move(10, 20);
        maximizedWindow.maximize();

        assertTrue(maximizedWindow.getCssValue("z-index")
                .compareTo(anotherWindow.getCssValue("z-index")) > 0);
    }
}
