package com.vaadin.tests.components.datefield;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PopupDateFieldStatesTest extends MultiBrowserTest {

    @Test
    public void readOnlyDateFieldPopupShouldNotOpen() throws IOException,
            InterruptedException {
        openTestURL();

        compareScreen("dateFieldStates");
    }

}
