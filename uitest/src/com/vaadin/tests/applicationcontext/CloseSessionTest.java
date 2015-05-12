package com.vaadin.tests.applicationcontext;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.FixedNotificationElement;

public class CloseSessionTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    /**
     * Close, reload and assert there's a new VaadinServiceSession in the old
     * HttpSession.
     */
    @Test
    public void testCloseAndReopen() {
        clickButton("Close VaadinServiceSession and reopen page");
        assertLogText(2, "4. Same hash as current? false");
        assertLogText(0, "6. Same WrappedSession id? true");
    }

    /**
     * Invalidate, reload and assert there's a new VaadinServiceSession in a new
     * HttpSession.
     */
    @Test
    public void testInvalidateHttpSessionAndReopen() {
        clickButton("Invalidate HttpSession and reopen page");
        assertLogText(2, "4. Same hash as current? false");
        assertLogText(0, "6. Same WrappedSession id? false");
    }

    /**
     * Test closing session and redirecting to another page.
     */
    @Test
    public void testCloseVaadinServiceAndRedirect() {
        clickButton("Close VaadinServiceSession and redirect elsewhere");
        Assert.assertEquals("Unexpected page contents,",
                "This is a static file", findElement(By.xpath("//h1"))
                        .getText());
    }

    /**
     * Verify we get a Session Expired error if doing something after closing
     * the VaadinSession.
     */
    @Test
    public void testCloseVaadinSession() {
        String caption = "Just close VaadinSession";
        clickButton(caption);
        clickButton(caption);
        assertSessionExpired();
    }

    /**
     * Verify we get a Session Expired error if doing something after closing
     * the HttpSession.
     */
    @Test
    public void testCloseHttpSession() {
        String caption = "Just close HttpSession";
        clickButton(caption);
        clickButton(caption);
        assertSessionExpired();
    }

    /**
     * Verify we get a Session Expired error if closing HttpSession in a
     * background thread.
     */
    @Test
    public void testBackgroundThreadHttpSessionInvalidation()
            throws InterruptedException {
        String caption = "Invalidate HttpSession in a background thread";
        clickButton(caption);
        sleep(2000);
        clickButton(caption);
        assertSessionExpired();
    }

    private void assertLogText(int index, String expected) {
        Assert.assertEquals("Unexpected log text,", expected, getLogRow(index));
    }

    private void assertSessionExpired() {
        String expected = "Session Expired";
        String actual = $(FixedNotificationElement.class).first().getCaption();
        Assert.assertEquals("Unexpected notification,", actual, expected);
    }

    public void clickButton(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }
}
