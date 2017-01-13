package com.vaadin.tests.components.uitest;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.customelements.AbstractDateFieldElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class ThemeTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    protected Class<?> getUIClass() {
        return ThemeTestUI.class;
    }

    protected abstract String getTheme();

    @Test
    public void testTheme() throws Exception {
        openTestURL("theme=" + getTheme());
        runThemeTest();
    }

    private void runThemeTest() throws IOException {
        TabSheetElement themeTabSheet = $(TabSheetElement.class).first();

        // Labels tab
        compareScreen("labels");

        // Buttons tab
        openTab(themeTabSheet, "Buttons");
        compareScreen("buttons");

        // Embedded tab
        openTab(themeTabSheet, "Embedded");
        compareScreen("embedded");

        // Dates tab
        openTab(themeTabSheet, "Dates");
        testDates();

        // TextFields tab
        openTab(themeTabSheet, "TextFields");
        compareScreen("textfields");

        // Selects tab
        openTab(themeTabSheet, "Selects");
        testSelects();

        // Sliders tab
        openTab(themeTabSheet, "Sliders");
        compareScreen("sliders");

        // Uploads tab
        openTab(themeTabSheet, "Uploads");
        compareScreen("uploads");

        // Forms tab
        openTab(themeTabSheet, "Forms");
        compareScreen("forms");

        // Tables tab
        openTab(themeTabSheet, "Tables");
        testTables();

        // Trees tab
        openTab(themeTabSheet, "Trees");
        compareScreen("trees");

        // TreeTable tab
        openTab(themeTabSheet, "TreeTable");
        compareScreen("treetable");

        // Layouts tab
        openTab(themeTabSheet, "Layouts");
        compareScreen("layouts");

        // TabSheets tab
        openTab(themeTabSheet, "TabSheets");
        compareScreen("tabsheets");

        // Accordions tab
        openTab(themeTabSheet, "Accordions");
        compareScreen("accordions");

        // Windows tab
        openTab(themeTabSheet, "Windows");
        testWindows();

        // Notifications tab
        openTab(themeTabSheet, "Notifications");
        testNotifications();
    }

    private void openTab(TabSheetElement themeTabSheet, String string) {
        themeTabSheet.openTab(string);
        /* Layouting takes a moment after tab has been opened. */
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
        }

    }

    private void testNotifications() throws IOException {
        testNotification(0, "notification-humanized");
        testNotification(1, "notification-warning");
        testNotification(2, "notification-error");
        testNotification(3, "notification-tray");
    }

    private void testNotification(int id, String identifier)
            throws IOException {
        $(ButtonElement.class).id("notifButt" + id).click();
        compareScreen(identifier);
        $(NotificationElement.class).first().close();
    }

    protected void testWindows() throws IOException {
        testWindow(0, "subwindow-default");
    }

    protected void testWindow(int id, String identifier) throws IOException {
        $(ButtonElement.class).id("windButton" + id).click();
        compareScreen(identifier);
        WindowElement window = $(WindowElement.class).first();
        if (getTheme() == "chameleon"
                && BrowserUtil.isIE(getDesiredCapabilities())) {
            new Actions(getDriver()).moveToElement(window, 10, 10).click()
                    .sendKeys(Keys.ESCAPE).perform();
        } else {
            window.findElement(By.className("v-window-closebox")).click();
        }
    }

    private void testTables() throws IOException {
        compareScreen("tables");
        TableElement table = $(TableElement.class).first();
        new Actions(driver).moveToElement(table.getCell(0, 1), 5, 5)
                .contextClick().perform();
        compareScreen("tables-contextmenu");
        table.findElement(By.className("v-table-column-selector")).click();
        compareScreen("tables-collapsemenu");
    }

    private void testSelects() throws IOException {
        compareScreen("selects");
        $(ComboBoxElement.class).id("select0").openPopup();
        compareScreen("selects-first-open");
        $(ComboBoxElement.class).id("select1").openPopup();
        compareScreen("selects-second-open");
        $(ComboBoxElement.class).id("select6").openPopup();
        compareScreen("selects-third-open");

        /* In chameleon theme search combobox has no visible popup button */
        ComboBoxElement searchComboBox = $(ComboBoxElement.class).id("select7");
        if (searchComboBox.findElement(By.tagName("div")).isDisplayed()) {
            searchComboBox.openPopup();
        } else {
            WebElement textBox = searchComboBox
                    .findElement(By.vaadin("#textbox"));
            textBox.click();
            textBox.sendKeys(Keys.ARROW_DOWN);
        }
        compareScreen("selects-fourth-open");

        $(ComboBoxElement.class).id("select8").openPopup();
        compareScreen("selects-fifth-open");
    }

    private void testDates() throws IOException {
        compareScreen("dates");
        $(AbstractDateFieldElement.class).id("datefield0").openPopup();
        compareScreen("dates-first-popup");
        $(AbstractDateFieldElement.class).id("datefield1").openPopup();
        compareScreen("dates-second-popup");
        $(AbstractDateFieldElement.class).id("datefield2").openPopup();
        compareScreen("dates-third-popup");
        $(AbstractDateFieldElement.class).id("datefield3").openPopup();
        compareScreen("dates-fourth-popup");
    }
}
