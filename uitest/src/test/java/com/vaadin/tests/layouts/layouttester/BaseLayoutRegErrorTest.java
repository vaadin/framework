package com.vaadin.tests.layouts.layouttester;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public abstract class BaseLayoutRegErrorTest extends MultiBrowserTest {

    @Test
    public void LayoutRegError() throws IOException {
        openTestURL();
        compareScreen("RegError");
    }

}