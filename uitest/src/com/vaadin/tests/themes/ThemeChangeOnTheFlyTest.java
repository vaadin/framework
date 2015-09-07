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
package com.vaadin.tests.themes;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ThemeChangeOnTheFlyTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Seems like stylesheet onload is not fired on PhantomJS
        // https://github.com/ariya/phantomjs/issues/12332
        return getBrowsersExcludingPhantomJS();
    }

    @Test
    public void injectedStyleAndThemeChange() throws IOException {
        openTestURL();
        $(ButtonElement.class).caption("Inject blue background").first()
                .click();
        changeTheme("runo");
        compareScreen("runo-blue-background");
    }

    @Test
    public void reindeerToOthers() throws IOException {
        openTestURL();
        compareScreen("reindeer");

        changeThemeAndCompare("runo");
        changeThemeAndCompare("chameleon");
        changeThemeAndCompare("base");

    }

    @Test
    public void runoToReindeer() throws IOException {
        openTestURL("theme=runo");
        compareScreen("runo");
        changeThemeAndCompare("reindeer");
    }

    @Test
    public void reindeerToNullToReindeer() throws IOException {
        openTestURL();

        changeTheme("null");
        changeThemeAndCompare("reindeer");
    }

    private void changeThemeAndCompare(String theme) throws IOException {
        changeTheme(theme);
        compareScreen(theme);
    }

    private void changeTheme(String theme) {
        $(ButtonElement.class).id(theme).click();
        if (theme.equals("null")) {
            waitForThemeToChange("");
            assertOverlayTheme("");
        } else {
            waitForThemeToChange(theme);
            assertOverlayTheme(theme);
        }
    }

    private void assertOverlayTheme(String theme) {
        final WebElement overlayContainerDiv = findElement(By
                .xpath("//div[contains(@class,'v-overlay-container')]"));
        String overlayClass = overlayContainerDiv.getAttribute("class").trim();

        assertThat(overlayClass, containsString(theme));
    }

}
