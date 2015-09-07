/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ThemeChangeFaviconTest extends SingleBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Seems like stylesheet onload is not fired on PhantomJS
        // https://github.com/ariya/phantomjs/issues/12332
        return Collections.singletonList(Browser.FIREFOX
                .getDesiredCapabilities());
    }

    @Test
    public void changeFavicon() throws InterruptedException {
        setDebug(true);
        openTestURL();
        assertFavicon("reindeer");

        changeTheme("valo");
        assertFavicon("valo");

        changeTheme("reindeer");
        assertFavicon("reindeer");
    }

    private void changeTheme(final String theme) {
        $(ButtonElement.class).caption(theme).first().click();
        waitForThemeToChange(theme);
    }

    private void assertFavicon(String theme) {
        String faviconUrl = "/VAADIN/themes/" + theme + "/favicon.ico";

        List<WebElement> elements = findElements(By
                .cssSelector("link[rel~=\"icon\"]"));

        Assert.assertEquals(2, elements.size());

        for (WebElement element : elements) {
            Assert.assertTrue(element.getAttribute("href")
                    + " does not end with " + faviconUrl,
                    element.getAttribute("href").endsWith(faviconUrl));
        }
    }

}
