package com.vaadin.tests.components.popupview;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PopupViewAndFragmentTest extends MultiBrowserTest {

    @Test
    public void changeFragmentAndOpenPopupView() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        // Wait for popup view to fully open
        sleep(1000);
        compareScreen("changedFragment");
    }
}
