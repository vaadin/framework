package com.vaadin.tests.components.window;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RepaintWindowContentsTest extends MultiBrowserTest {

    @Test
    public void testRepaintWindowContents() throws Exception {
        openTestURL();
        assertWindowContents("Button 1");
        toggleWindowContents();
        assertWindowContents("Button 2");
        toggleWindowContents();
        assertWindowContents("Button 1");
        toggleWindowContents();
        assertWindowContents("Button 2");
    }

    private void toggleWindowContents() {
        getWindowButton().click();
    }

    private void assertWindowContents(String expected) {
        Assert.assertEquals("Unexpected window contents,", expected,
                getWindowButton().getText());
    }

    private ButtonElement getWindowButton() {
        return $(WindowElement.class).$(ButtonElement.class).first();
    }
}
