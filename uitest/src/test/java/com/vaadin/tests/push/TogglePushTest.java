package com.vaadin.tests.push;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public class TogglePushTest extends MultiBrowserTest {

    @Test
    public void togglePushInInit() throws Exception {
        setPush(true);
        String url = getTestUrl();

        // Open with push disabled
        driver.get(addParameter(url, "push=disabled"));

        assertFalse(getPushToggle().isSelected());

        getDelayedCounterUpdateButton().click();
        sleep(2000);
        assertEquals("Counter has been updated 0 times", getCounterText());

        // Open with push enabled
        driver.get(addParameter(url, "push=enabled"));
        assertTrue(getPushToggle().isSelected());

        getDelayedCounterUpdateButton().click();
        sleep(2000);
        assertEquals("Counter has been updated 1 times", getCounterText());

    }

    private String addParameter(String url, String queryParameter) {
        if (url.contains("?")) {
            return url + "&" + queryParameter;
        } else {
            return url + "?" + queryParameter;
        }
    }

    @Test
    public void togglePush() throws InterruptedException {
        setPush(true);
        openTestURL();
        getDelayedCounterUpdateButton().click();
        sleep(2000);

        // Push is enabled, so text gets updated
        assertEquals("Counter has been updated 1 times", getCounterText());

        // Disable push
        getPushToggle().click();
        getDelayedCounterUpdateButton().click();
        sleep(2000);
        // Push is disabled, so text is not updated
        assertEquals("Counter has been updated 1 times", getCounterText());

        getDirectCounterUpdateButton().click();
        // Direct update is visible, and includes previous update
        assertEquals("Counter has been updated 3 times", getCounterText());

        // Re-enable push
        getPushToggle().click();
        getDelayedCounterUpdateButton().click();
        sleep(2000);

        // Push is enabled again, so text gets updated
        assertEquals("Counter has been updated 4 times", getCounterText());
    }

    private WebElement getDirectCounterUpdateButton() {
        return vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[2]/VButton[0]/domChild[0]/domChild[0]");
    }

    private WebElement getPushToggle() {
        return vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[1]/VCheckBox[0]/domChild[0]");
    }

    private WebElement getDelayedCounterUpdateButton() {
        return vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[3]/VButton[0]/domChild[0]/domChild[0]");
    }

    private String getCounterText() {
        return vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VLabel[0]")
                        .getText();
    }

}
