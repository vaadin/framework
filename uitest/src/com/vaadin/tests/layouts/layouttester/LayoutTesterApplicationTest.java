/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.layouts.layouttester;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class LayoutTesterApplicationTest extends MultiBrowserTest {

    @Override
    protected DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities cap = new DesiredCapabilities(
                super.getDesiredCapabilities());
        cap.setCapability("nativeEvents", false);
        return cap;
    }

    Map<String, Integer> numberOfSubTests = new HashMap<String, Integer>();
    private Set<String> tableOrIconsTests = new HashSet<String>();

    {
        numberOfSubTests.put("getExpandRatiosTests", 3);
        numberOfSubTests.put("getLayoutSizingTests", 4);
        numberOfSubTests.put("getComponentAddReplaceMoveTests", 4);
        numberOfSubTests.put("getMarginSpacingTests", 4);
        numberOfSubTests.put("getComponentSizingTests", 3);

        tableOrIconsTests.add("getComponentSizingTests");
        tableOrIconsTests.add("getExpandRatiosTests");
        tableOrIconsTests.add("getLayoutSizingTests");
        tableOrIconsTests.add("getMarginSpacingTests");
        tableOrIconsTests.add("getIconsTests");

    }

    @Test
    public void verticalLayout() throws Exception {
        openTestURL();
        runTest(VerticalLayout.class);
    }

    @Test
    public void horizontalLayout() throws Exception {
        openTestURL();
        runTest(HorizontalLayout.class);
    }

    @Test
    public void gridLayout() throws Exception {
        numberOfSubTests.put("getComponentAddReplaceMoveTests", 6);
        numberOfSubTests.put("getComponentSizingTests", 4);
        numberOfSubTests.put("getExpandRatiosTests", 4);

        openTestURL();
        runTest(GridLayout.class);
    }

    private void runTest(Class<?> layoutClass) throws Exception {
        new Select(vaadinElementById("layoutSelect").findElement(
                By.xpath("select")))
                .selectByVisibleText(layoutClass.toString());
        focusElementWithId("nextButton");

        for (String subTest : LayoutTesterApplication.layoutGetters) {
            compareScreen(subTest);
            Integer subTests = numberOfSubTests.get(subTest);
            if (subTests != null) {
                for (int i = 1; i <= subTests; i++) {
                    clickAndCompareScreen(subTest, "testButton" + i);
                }
            }
            getNextButton().click();
        }

    }

    /**
     * @param elementId
     *            the id of the element to focus
     */
    private void focusElementWithId(String elementId) {
        // This should really be in TestBench
        ((JavascriptExecutor) getDriver())
                .executeScript("document.getElementById('" + elementId
                        + "').focus()");
    }

    /**
     * Clicks the button with the given id and compares the result to a
     * screenshot named 'screenshotPrefix'-buttonCaption.
     * 
     * @param screenshotPrefix
     * @param buttonId
     * @throws Exception
     */
    private void clickAndCompareScreen(String screenshotPrefix, String buttonId)
            throws Exception {
        WebElement button = vaadinElementById(buttonId);
        button.click();
        if (needsDelayToStabilize(screenshotPrefix)) {
            // Table does some extra layout phase and TestBench does not always
            // take this into account, grabbing screenshots before the layout
            // phase is done (see #12866).
            sleep(200);
        }
        compareScreen(screenshotPrefix + "-" + sanitize(button.getText()));
    }

    private boolean needsDelayToStabilize(String screenshotPrefix) {
        return tableOrIconsTests.contains(screenshotPrefix);
    }

    private String sanitize(String text) {
        return text.replace("%", "pct").replaceAll("[^a-zA-Z0-9]", "-");
    }

    private WebElement getNextButton() {
        return vaadinElementById(LayoutTesterApplication.NEXT_BUTTON_ID);
    }
}
