package com.vaadin.tests.themes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.EmbeddedElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class LegacyComponentThemeChangeTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // For some reason, IE times out when trying to open the combobox,
        // #18341
        return getBrowsersExcludingIE();
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
        if (BrowserUtil.isChrome(getDesiredCapabilities())) {
            // Chrome 47 won't initialize the dummy flash properly
            return;
        }
        EmbeddedElement e = $(EmbeddedElement.class).first();
        WebElement movieParam = e
                .findElement(By.xpath(".//param[@name='movie']"));
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
        WebElement popup = findElement(
                By.className("v-filterselect-suggestpopup"));
        WebElement itemImage = popup.findElement(By.xpath(".//img"));
        assertAttributePrefix(itemImage, "src", theme);
    }

    private void assertMenubarTheme(String theme) {
        // The runoImage must always come from Runo
        WebElement runoImage = $(MenuBarElement.class).first()
                .findElement(By.xpath(".//span[text()='runo']/img"));
        String runoImageSrc = runoImage.getAttribute("src");

        // Something in Selenium normalizes the image so it becomes
        // "/themes/runo/icons/16/ok.png" here although it is
        // "/themes/<currenttheme>/../runo/icons/16/ok.png" in the browser
        assertEquals(getThemeURL("runo") + "icons/16/ok.png", runoImageSrc);

        // The other image should change with the theme
        WebElement themeImage = $(MenuBarElement.class).first()
                .findElement(By.xpath(".//span[text()='selectedtheme']/img"));
        assertAttributePrefix(themeImage, "src", theme);

        WebElement subMenuItem = $(MenuBarElement.class).first()
                .findElement(By.xpath(".//span[text()='sub menu']"));
        subMenuItem.click();

        WebElement subMenu = findElement(By.className("v-menubar-popup"));
        WebElement subMenuRuno = subMenu
                .findElement(By.xpath(".//span[text()='runo']/img"));
        String subMenuRunoImageSrc = subMenuRuno.getAttribute("src");
        assertEquals(getThemeURL("runo") + "icons/16/ok.png",
                subMenuRunoImageSrc);
        WebElement subMenuThemeImage = subMenu
                .findElement(By.xpath(".//span[text()='selectedtheme']/img"));
        assertAttributePrefix(subMenuThemeImage, "src", theme);

        // Close menu item.
        subMenuItem.click();
    }

    private void assertAttributePrefix(WebElement element, String attribute,
            String theme) {
        String value = element.getAttribute(attribute);
        String expectedPrefix = getThemeURL(theme);
        assertTrue(
                "Attribute " + attribute + "='" + value
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

}
