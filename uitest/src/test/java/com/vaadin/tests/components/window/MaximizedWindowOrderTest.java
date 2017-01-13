package com.vaadin.tests.components.window;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.testbench.elements.ButtonElement;
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

        assertThat(anotherWindow.getCssValue("z-index"),
                is(greaterThan(maximizedWindow.getCssValue("z-index"))));

        assertThat(getMaximizedWindow().getCssValue("z-index"), is("10000"));
        assertThat(getAnotherWindow().getCssValue("z-index"), is("10001"));
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

        assertThat(maximizedWindow.getCssValue("z-index"),
                is(greaterThan(anotherWindow.getCssValue("z-index"))));
    }
}
