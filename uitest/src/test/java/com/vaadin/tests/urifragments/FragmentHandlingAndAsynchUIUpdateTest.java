package com.vaadin.tests.urifragments;

import static com.vaadin.tests.urifragments.FragmentHandlingAndAsynchUIUpdate.FRAG_NAME_TPL;
import static com.vaadin.tests.urifragments.FragmentHandlingAndAsynchUIUpdate.START_FRAG_ID;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Back and Forward buttons in browser should work correctly during UI update
 *
 * @author Vaadin Ltd
 */
public class FragmentHandlingAndAsynchUIUpdateTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // IE web driver fails to read fragment properly, these must be tested
        // manually. See
        // https://github.com/SeleniumHQ/selenium-google-code-issue-archive/issues/7966
        return getBrowsersExcludingIE();
    }

    /**
     * The case when we successively set 10 fragments, go back 9 times and then
     * go forward 9 times
     */
    @Test
    public void testBackwardForwardHistoryWithWaitingForSettingFrag()
            throws Exception {
        openTestURL();

        for (int i = 0; i < 10; i++) {
            // here we wait for setting fragment in URI. If not to do it -
            // history will be "loss"
            getDriver()
                    .findElement(
                            By.id(FragmentHandlingAndAsynchUIUpdate.BUTTON_ID))
                    .click();
            assertFragment(String.format(FRAG_NAME_TPL, START_FRAG_ID + i));
        }

        for (int i = 8; i >= 0; i--) {
            ((JavascriptExecutor) driver).executeScript("history.back()");
            assertFragment(String.format(FRAG_NAME_TPL, START_FRAG_ID + i));
        }

        for (int i = 1; i < 10; i++) {
            ((JavascriptExecutor) driver).executeScript("history.forward()");
            assertFragment(String.format(FRAG_NAME_TPL, START_FRAG_ID + i));
        }
    }

    /**
     * The case when it seems than history is loss
     */
    @Test
    public void testBackwardForwardHistoryWithoutWaitingForSettingFrag()
            throws Exception {
        openTestURL();

        // begin to wait for setting 3th fragment and then click backward
        // history and then wait for 9th fragment
        new Thread(() -> {
            // wait for setting 3th fragment
            assertFragment(String.format(FRAG_NAME_TPL, START_FRAG_ID + 3));
            // go back one time after we wait for 3th fragment and then we
            // will see the "loss" of "forward history"
            ((JavascriptExecutor) driver).executeScript("history.back()");

            // now at some time new fragments will be set
            // wait for last fragment setting..
            // of course forward history is empty now..
            assertFragment(String.format(FRAG_NAME_TPL, START_FRAG_ID + 9));
        }).start();

        // not wait for setting fragment in URI
        // (simulated situation when users clicks some times in row on
        // button)
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                getDriver()
                        .findElement(By.id(
                                FragmentHandlingAndAsynchUIUpdate.BUTTON_ID))
                        .click();
            }
        }).start();
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
        }, 20);
    }

}
