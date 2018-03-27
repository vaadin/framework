package com.vaadin.tests.tb3;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

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
public abstract class MultiBrowserTest extends PrivateTB3Configuration {

    protected List<DesiredCapabilities> getBrowsersSupportingWebSocket() {
        // No WebSocket support in IE8-9 and PhantomJS
        return getBrowserCapabilities(Browser.IE10, Browser.IE11,
                Browser.FIREFOX, Browser.CHROME);
    }

    protected List<DesiredCapabilities> getBrowsersExcludingPhantomJS() {
        return getBrowserCapabilities(Browser.IE8, Browser.IE9, Browser.IE10,
                Browser.IE11, Browser.CHROME, Browser.FIREFOX);
    }

    protected List<DesiredCapabilities> getBrowsersExcludingIE() {
        return getBrowserCapabilities(Browser.FIREFOX, Browser.CHROME,
                Browser.PHANTOMJS);
    }

    protected List<DesiredCapabilities> getBrowsersExcludingFirefox() {
        // this is sometimes needed as the Firefox driver causes extra mouseOut
        // events that make tooltips disappear etc.
        return getBrowserCapabilities(Browser.IE8, Browser.IE9, Browser.IE10,
                Browser.IE11, Browser.CHROME, Browser.PHANTOMJS);
    }

    protected List<DesiredCapabilities> getBrowsersExcludingIE8() {
        return getBrowserCapabilities(Browser.IE9, Browser.IE10, Browser.IE11,
                Browser.FIREFOX, Browser.CHROME, Browser.PHANTOMJS);
    }

    protected List<DesiredCapabilities> getBrowsersSupportingShiftClick() {
        return getBrowserCapabilities(Browser.IE8, Browser.IE9, Browser.IE10,
                Browser.IE11, Browser.CHROME);
    }

    protected List<DesiredCapabilities> getIEBrowsersOnly() {
        return getBrowserCapabilities(Browser.IE8, Browser.IE9, Browser.IE10,
                Browser.IE11);
    }

    protected List<DesiredCapabilities> getBrowsersSupportingContextMenu() {
        // context menu doesn't work in phantom JS and works weirdly with IE8
        // and selenium.
        return getBrowserCapabilities(Browser.IE9, Browser.IE10, Browser.IE11,
                Browser.FIREFOX, Browser.CHROME);
    }

    protected List<DesiredCapabilities> getBrowsersSupportingTooltip() {
        // With IEDriver, the cursor seems to jump to default position after the
        // mouse move, so we are not able to test the tooltip behaviour properly
        // unless using requireWindowFocusForIE() { return true; } .
        // See #13854.
        // On Firefox, the driver causes additional mouseOut events causing the
        // tooltip to disappear immediately. Tooltips may work in some
        // particular cases, but not in general.
        return getBrowserCapabilities(Browser.CHROME, Browser.PHANTOMJS);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Uncomment Safari and Opera if those become tested browsers again.
        return getBrowserCapabilities(Browser.IE8, Browser.IE9, Browser.IE10,
                Browser.IE11, Browser.FIREFOX, Browser.CHROME,
                Browser.PHANTOMJS /* , Browser.SAFARI, Browser.OPERA */);
    }

    protected List<DesiredCapabilities> getBrowserCapabilities(
            Browser... browsers) {
        List<DesiredCapabilities> capabilities = new ArrayList<DesiredCapabilities>();
        for (Browser browser : browsers) {
            capabilities.add(browser.getDesiredCapabilities());
        }
        return capabilities;
    }
}
