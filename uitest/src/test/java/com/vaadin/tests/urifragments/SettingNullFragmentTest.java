package com.vaadin.tests.urifragments;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * UI test: setting null as URI fragment clear (remove) the fragment in the
 * browser
 *
 * @author Vaadin Ltd
 */
public class SettingNullFragmentTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // IE web driver fails to read fragment properly, these must be tested
        // manually. See
        // https://github.com/SeleniumHQ/selenium-google-code-issue-archive/issues/7966
        return getBrowsersExcludingIE();
    }

    @Test
    public void testSettingNullURIFragment() throws Exception {
        openTestURL();

        navigateToFrag1();
        assertFragment(SettingNullFragment.FRAG_1_URI);

        navigateToNull();
        assertFragment(SettingNullFragment.NULL_FRAGMENT_URI);
    }

    private void assertFragment(String fragment) {
        final String expectedText = fragment;

        waitUntil(input -> {
            String currentURL = getDriver().getCurrentUrl();
            String currentURIFragment = "";
            if (currentURL.contains("#") && !currentURL.endsWith("#")) {
                currentURIFragment = currentURL.split("#")[1];
            }
            return expectedText.equals(currentURIFragment);
        });
    }

    private void navigateToFrag1() {
        hitButton(SettingNullFragment.BUTTON_FRAG_1_ID);
    }

    private void navigateToNull() {
        hitButton(SettingNullFragment.BUTTON_NULL_FRAGMENT_ID);
    }

}
