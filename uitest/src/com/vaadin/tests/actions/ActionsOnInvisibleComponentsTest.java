package com.vaadin.tests.actions;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ActionsOnInvisibleComponentsTest extends MultiBrowserTest {

    private static final String LAST_INIT_LOG = "3. 'C' triggers a click on a visible and enabled button";

    // This method should be removed once #12785 is fixed
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> browsers = super.getBrowsersToTest();
        // sendKeys does nothing on these browsers
        browsers.remove(Browser.FIREFOX.getDesiredCapabilities());
        browsers.remove(Browser.IE8.getDesiredCapabilities());
        browsers.remove(Browser.OPERA.getDesiredCapabilities());

        // Causes 'cannot focus element'
        browsers.remove(Browser.CHROME.getDesiredCapabilities());
        return browsers;
    }

    @Test
    public void testShortcutsOnInvisibleDisabledButtons() {
        openTestURL();
        Assert.assertEquals(LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("A");
        Assert.assertEquals(LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("B");
        Assert.assertEquals(LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("C");
        Assert.assertEquals("4. Click event for enabled button", getLogRow(0));
    }

    private void invokeShortcut(CharSequence key) {
        WebElement shortcutTarget = vaadinElementById("test-root");
        shortcutTarget.sendKeys(key);
    }
}
