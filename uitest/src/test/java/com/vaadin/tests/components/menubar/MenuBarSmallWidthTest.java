package com.vaadin.tests.components.menubar;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;

public class MenuBarSmallWidthTest extends MultiBrowserTest {

    @Test
    public void noIndexOutOfBoundsExceptionPresent() {
        openTestURL("debug");
        assertNoErrorNotifications();
    }
}
