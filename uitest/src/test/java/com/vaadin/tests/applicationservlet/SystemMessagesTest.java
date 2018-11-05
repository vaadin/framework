package com.vaadin.tests.applicationservlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SystemMessagesTest extends MultiBrowserTest {

    @Test
    public void testFinnishLocaleInSystemErrorMessage() throws Exception {
        openTestURL();
        verifyError("fi_FI");
    }

    @Test
    public void testGermanLocaleInSystemErrorMessage() throws Exception {
        openTestURL();
        $(NativeSelectElement.class).first().selectByText("de_DE");
        verifyError("de_DE");
    }

    private void verifyError(String locale) {
        $(ButtonElement.class).first().click();
        NotificationElement notification = $(NotificationElement.class).first();
        assertEquals("Incorrect notification caption,",
                notification.getCaption(), "Internal error");
        assertEquals("Incorrect notification description,",
                notification.getDescription(),
                "MessagesInfo locale: " + locale);
    }
}
