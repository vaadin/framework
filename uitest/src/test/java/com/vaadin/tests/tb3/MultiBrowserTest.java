package com.vaadin.tests.tb3;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.parallel.Browser;

/**
 * Base class for tests which should be run on all supported browsers. The test
 * is automatically launched for multiple browsers in parallel by the test
 * runner.
 *
 * Sub classes can, but typically should not, restrict the browsers used by
 * implementing a
 *
 * <pre>
 * &#064;Parameters
 * public static Collection&lt;DesiredCapabilities&gt; getBrowsersForTest() {
 * }
 * </pre>
 *
 * @author Vaadin Ltd
 */
@RunLocally(Browser.IE11)
public abstract class MultiBrowserTest extends PrivateTB3Configuration {

    protected List<DesiredCapabilities> getBrowsersExcludingChrome() {
        return getBrowserCapabilities(Browser.FIREFOX, Browser.IE11);
    }

    protected List<DesiredCapabilities> getBrowsersExcludingIE() {
        return getBrowserCapabilities(Browser.FIREFOX, Browser.CHROME);
    }

    protected List<DesiredCapabilities> getBrowsersExcludingFirefox() {
        return getBrowserCapabilities(Browser.IE11, Browser.CHROME);
    }

    protected List<DesiredCapabilities> getBrowsersSupportingShiftClick() {
        return getBrowserCapabilities(Browser.IE11, Browser.CHROME);
    }

    protected List<DesiredCapabilities> getIEBrowsersOnly() {
        return getBrowserCapabilities(Browser.IE11);
    }

    protected List<DesiredCapabilities> getBrowsersSupportingTooltip() {
        // With IEDriver, the cursor seems to jump to default position after the
        // mouse move, so we are not able to test the tooltip behavior properly
        // unless using requireWindowFocusForIE() { return true; } .
        // See #13854.
        // On Firefox, the driver causes additional mouseOut events causing the
        // tooltip to disappear immediately. Tooltips may work in some
        // particular cases, but not in general.
        return getBrowserCapabilities(Browser.CHROME);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.IE11, Browser.FIREFOX,
                Browser.CHROME);
    }

    protected List<DesiredCapabilities> getBrowserCapabilities(
            Browser... browsers) {
        List<DesiredCapabilities> capabilities = new ArrayList<>();
        for (Browser browser : browsers) {
            capabilities.add(browser.getDesiredCapabilities());
        }
        return capabilities;
    }
}
