package com.vaadin.tests.htmlimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class HtmlImportUITest extends MultiBrowserTest {

    @Test
    public void importsLoadedAfterJs() {
        openTestURL();
        WebElement log = findElement(By.id("clientlog")); // Defined by ui.js

        List<WebElement> messages = log.findElements(By.className("message"));
        // assertEquals("Some log messages are missing or extra", 3,
        // messages.size());

        // JS before HTML, UI deps in bootstrap, rest dynamically

        // ui.js just sets up the logging
        assertEquals("ui.html", messages.get(0).getText());

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
                assertFalse("Multiple HTML imports for " + file,
                        htmlImportIndexes.containsKey(file));
                htmlImportIndexes.put(file, i);
            } else if (e.getTagName().equalsIgnoreCase("script")) {
                // JS
                String src = e.getAttribute("src");
                String file = src.substring(src.lastIndexOf('/') + 1);
                assertFalse("Multiple script tags for " + file,
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

        assertTrue("super js should be before super html",
                superJsIndex < superHtmlIndex);

        assertTrue("super dependencies should be before sub js",
                superHtmlIndex < subJsIndex);

        assertTrue("sub js should be before sub html",
                subJsIndex < subHtmlIndex);
    }
}
