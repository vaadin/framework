package com.vaadin.tests.layouts.layouttester;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 *
 * @author Vaadin Ltd
 */
public abstract class BaseIconTest extends MultiBrowserTest {

    @Test
    public void LayoutIcon() throws IOException {
        openTestURL();
        compareScreen("icon");
    }

}
