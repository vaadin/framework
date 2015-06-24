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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.EmbeddedElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class LegacyComponentThemeChangeTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Seems like stylesheet onload is not fired on PhantomJS
        // https://github.com/ariya/phantomjs/issues/12332
        List<DesiredCapabilities> l = getBrowsersExcludingPhantomJS();

        // For some reason, IE times out when trying to open the combobox,
        // #18341
        l.remove(Browser.IE11.getDesiredCapabilities());
        return l;
    }

    @Test
    public void legacyComponentThemeResourceChange() {
        openTestURL();
        String theme = "reindeer";
        assertMenubarTheme(theme);
        assertCombobBoxTheme(theme);
        assertTableTheme(theme);
        assertEmbeddedTheme(theme);

        theme = "runo";
        changeTheme(theme);
        assertMenubarTheme(theme);
        assertCombobBoxTheme(theme);
        assertTableTheme(theme);
        assertEmbeddedTheme(theme);

        theme = "reindeer";
        changeTheme(theme);
        assertMenubarTheme(theme);
        assertCombobBoxTheme(theme);
        assertTableTheme(theme);
        assertEmbeddedTheme(theme);

    }

    private void assertEmbeddedTheme(String theme) {
        if (BrowserUtil.isIE8(getDesiredCapabilities())) {
            // IE8 won't initialize the dummy flash properly
            return;
        }
        EmbeddedElement e = $(EmbeddedElement.class).first();
        WebElement movieParam = e.findElement(By
                .xpath(".//param[@name='movie']"));
        WebElement embed = e.findElement(By.xpath(".//embed"));
        assertAttributePrefix(movieParam, "value", theme);
        assertAttributePrefix(embed, "src", theme);
        assertAttributePrefix(embed, "movie", theme);
    }

    private void assertTableTheme(String theme) {
        TableElement t = $(TableElement.class).first();
        t.getRow(0).contextClick();
        WebElement popup = findElement(By.className("v-contextmenu"));

        WebElement actionImage = popup.findElement(By.xpath(".//img"));
        assertAttributePrefix(actionImage, "src", theme);
    }

    private void assertCombobBoxTheme(String theme) {
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        WebElement selectedImage = cb.findElement(By.xpath("./img"));
        assertAttributePrefix(selectedImage, "src", theme);

        cb.openPopup();
        WebElement popup = findElement(By
                .className("v-filterselect-suggestpopup"));
        WebElement itemImage = popup.findElement(By.xpath(".//img"));
        assertAttributePrefix(itemImage, "src", theme);
    }

    private void assertMenubarTheme(String theme) {
        // The runoImage must always come from Runo
        WebElement runoImage = $(MenuBarElement.class).first().findElement(
                By.xpath(".//span[text()='runo']/img"));
        String runoImageSrc = runoImage.getAttribute("src");

        // Something in Selenium normalizes the image so it becomes
        // "/themes/runo/icons/16/ok.png" here although it is
        // "/themes/<currenttheme>/../runo/icons/16/ok.png" in the browser
        Assert.assertEquals(getThemeURL("runo") + "icons/16/ok.png",
                runoImageSrc);

        // The other image should change with the theme
        WebElement themeImage = $(MenuBarElement.class).first().findElement(
                By.xpath(".//span[text()='seletedtheme']/img"));
        assertAttributePrefix(themeImage, "src", theme);
    }

    private void assertAttributePrefix(WebElement element, String attribute,
            String theme) {
        String value = element.getAttribute(attribute);
        String expectedPrefix = getThemeURL(theme);
        Assert.assertTrue("Attribute " + attribute + "='" + value
                + "' does not start with " + expectedPrefix,
                value.startsWith(expectedPrefix));

    }

    private String getThemeURL(String theme) {
        return getBaseURL() + "/VAADIN/themes/" + theme + "/";
    }

    private void changeTheme(String theme) {
        $(ButtonElement.class).id(theme).click();
        waitForThemeToChange(theme);
    }

    private void waitForThemeToChange(final String theme) {

        final WebElement rootDiv = findElement(By
                .xpath("//div[contains(@class,'v-app')]"));
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                String rootClass = rootDiv.getAttribute("class").trim();

                return rootClass.contains(theme);
            }
        }, 30);
    }

}
