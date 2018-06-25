package com.vaadin.tests.components.gridlayout;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class UniformGridLayoutUITest extends MultiBrowserTest {

    @Test
    public void noncollapsed() throws Exception {
        openTestURL();
        compareScreen("noncollapsed");
    }

    @Test
    public void collapsed() throws Exception {
        openTestURL("collapse");
        compareScreen("collapsed");
    }
}
