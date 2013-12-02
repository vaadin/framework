package com.vaadin.tests.components.textfield;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TextFieldTruncatesUnderscoresInModalDialogsTest extends
        MultiBrowserTest {

    @Test
    public void testWindowRepositioning() throws Exception {
        openTestURL();

        compareScreen("TextFieldTruncatesUnderscoresInModalDialogs");
    }
}
