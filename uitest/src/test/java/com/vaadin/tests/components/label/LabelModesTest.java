package com.vaadin.tests.components.label;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class LabelModesTest extends MultiBrowserTest {

    @Test
    public void testLabelModes() throws Exception {
        openTestURL();
        compareScreen("labelmodes");
    }

}
