/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.htmlimport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class HtmlImportUITest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {

        List<DesiredCapabilities> browsers = getBrowsersExcludingPhantomJS();
        browsers.add(PHANTOMJS2());

        return browsers.stream().filter(dc -> {
            // Won't work on Firefox 24, will work when testing is done on a
            // modern Firefox
            if (BrowserUtil.isFirefox(dc) && dc.getVersion().equals("24")) {
                return false;
            }

            return true;

        }).collect(Collectors.toList());
    }

    @Test
    public void importsLoadedAfterJs() {
        openTestURL();
        WebElement log = findElement(By.id("clientlog")); // Defined by ui.js

        List<WebElement> messages = log.findElements(By.className("message"));
        // Assert.assertEquals("Some log messages are missing or extra", 3,
        // messages.size());

        // JS before HTML, UI deps in bootstrap, rest dynamically

        // ui.js just sets up the logging
        Assert.assertEquals("ui.html", messages.get(0).getText());

        // Apparently Chrome does not guarantee that "label.js" is executed
        // before "label.html", at least in the way we are loading HTML and JS.
        // Therefore, this just checks the order of the import statements

        List<WebElement> headContents = findElements(By.xpath("//head/*"));
        Map<String, Integer> htmlImportIndexes = new HashMap<String, Integer>();
        Map<String, Integer> jsIndexes = new HashMap<String, Integer>();

        for (int i = 0; i < headContents.size(); i++) {
            WebElement e = headContents.get(i);
            if (e.getTagName().equalsIgnoreCase("link")
                    && e.getAttribute("rel").equalsIgnoreCase("import")) {
                // HTML import
                String href = e.getAttribute("href");
                String file = href.substring(href.lastIndexOf('/') + 1);
                Assert.assertFalse("Multiple HTML imports for " + file,
                        htmlImportIndexes.containsKey(file));
                htmlImportIndexes.put(file, i);
            } else if (e.getTagName().equalsIgnoreCase("script")) {
                // JS
                String src = e.getAttribute("src");
                String file = src.substring(src.lastIndexOf('/') + 1);
                Assert.assertFalse("Multiple script tags for " + file,
                        jsIndexes.containsKey(file));
                jsIndexes.put(file, i);
            }
        }

        // label.* + label2.* are from super + sub class loaded in
        // that defined order
        // labelX.* are on another component so it can come before or after

        int superJsIndex = jsIndexes.get("label.js");
        int superHtmlIndex = htmlImportIndexes.get("label.html");
        int subJsIndex = jsIndexes.get("label2.js");
        int subHtmlIndex = htmlImportIndexes.get("label2.html");
        int otherJsIndex = jsIndexes.get("labelX.js");
        int otherHtmlIndex = htmlImportIndexes.get("labelX.html");

        Assert.assertTrue("super js should be before super html",
                superJsIndex < superHtmlIndex);

        Assert.assertTrue("super dependencies should be before sub js",
                superHtmlIndex < subJsIndex);

        Assert.assertTrue("sub js should be before sub html",
                subJsIndex < subHtmlIndex);
    }
}
