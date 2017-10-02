package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIsInMultipleTabsTest extends MultiBrowserTest {

    @Test
    public void testPageReloadChangesUI() throws Exception {
        openTestURL();
        assertUI(1);
        openTestURL();
        assertUI(2);
        openTestURL("restartApplication");
        assertUI(1);
    }

    private void assertUI(int i) {
        assertEquals("Unexpected UI found,", "This is UI number " + i,
                $(LabelElement.class).first().getText());
    }
}
