package com.vaadin.tests.layouts.layouttester;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public abstract class BaseAlignmentTest extends MultiBrowserTest {

    @Test
    public void layoutAlignment() throws IOException {
        openTestURL();
        compareScreen("alignment");
    }
}
