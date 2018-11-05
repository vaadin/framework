package com.vaadin.tests.components.window;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Dimension;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MaximizeRestoreWindowWithManagedLayoutTest
        extends MultiBrowserTest {

    // This tests a timing issue so if this test fails randomly, it is
    // likely that something is broken
    @Test
    public void contentSizeCorrectAfterMaximizeRestore()
            throws InterruptedException {
        openTestURL();
        WindowElement window = $(WindowElement.class).first();
        TextFieldElement field = $(TextFieldElement.class).first();

        // Sleeps are here as there is no server request while resizing the
        // window so we must wait until the animation is done before measuring
        // and comparing
        assertSameWidth(window, field);
        window.maximize();
        sleep(200);
        assertSameWidth(window, field);
        window.restore();
        sleep(200);
        assertSameWidth(window, field);
        window.maximize();
        sleep(200);
        assertSameWidth(window, field);

    }

    private void assertSameWidth(WindowElement window, TextFieldElement field) {
        Dimension windowSize = window.getSize();
        Dimension fieldSize = field.getSize();
        assertEquals(windowSize.getWidth(), fieldSize.getWidth());
    }
}
