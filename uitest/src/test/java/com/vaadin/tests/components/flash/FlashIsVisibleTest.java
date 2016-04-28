package com.vaadin.tests.components.flash;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class FlashIsVisibleTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // FF and PhantomJS fail at Flash and ShiftClick
        return getBrowsersSupportingShiftClick();
    }

    @Test
    public void testFlashIsCorrectlyDisplayed() throws Exception {
        openTestURL();
        /* Allow the flash plugin to load before taking the screenshot */
        sleep(5000);
        compareScreen("blue-circle");
    }
}
