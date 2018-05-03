package com.vaadin.tests.components;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.Is.is;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if AbstractOrderedLayout displays captions correctly with
 * expanding ratios.
 *
 * @author Vaadin Ltd
 */
public class AbstractOrderedLayoutWithCaptionsTest extends MultiBrowserTest {

    @Test
    public void CaptionHeightMeasuredCorrectly() {
        openTestURL();

        WebElement div = getDriver()
                .findElement(By.cssSelector(".v-panel-content > div > div"));
        String paddingTop = div.getCssValue("padding-top");
        Integer paddingHeight = Integer
                .parseInt(paddingTop.substring(0, paddingTop.length() - 2));
        List<WebElement> children = getDriver()
                .findElements(By.cssSelector(".v-panel-content .v-slot"));
        assertThat(children.size(), is(3));

        Integer neededHeight = children.get(0).getSize().getHeight()
                + children.get(2).getSize().getHeight();

        if (BrowserUtil.isIE8(getDesiredCapabilities())) {
            // IE8 Reports the first element height incorrectly.
            --neededHeight;
        }
        assertThat(neededHeight, is(lessThanOrEqualTo(paddingHeight)));

    }
}
