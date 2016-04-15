package com.vaadin.tests.components.uitest;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elementsbase.ServerClass;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.FixedNotificationElement;
import com.vaadin.tests.tb3.newelements.WindowElement;

public abstract class ThemeTest extends MultiBrowserTest {

    @ServerClass("com.vaadin.ui.DateField")
    public static class DateFieldElement extends
            com.vaadin.testbench.elements.DateFieldElement {
        public void openPopup() {
            findElement(By.tagName("button")).click();
        }
    }

    @ServerClass("com.vaadin.ui.TabSheet")
    public static class TabSheetElement extends
            com.vaadin.testbench.elements.TabSheetElement {
        @Override
        public void openTab(String tabCaption) {
            super.openTab(tabCaption);
            /* Layouting takes a moment after tab has been opened. */
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
            }
        }
    }

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
        themeTabSheet.openTab("Buttons");
        compareScreen("buttons");

        // Embedded tab
        themeTabSheet.openTab("Embedded");
        compareScreen("embedded");

        // Dates tab
        themeTabSheet.openTab("Dates");
        testDates();

        // TextFields tab
        themeTabSheet.openTab("TextFields");
        compareScreen("textfields");

        // Selects tab
        themeTabSheet.openTab("Selects");
        testSelects();

        // Sliders tab
        themeTabSheet.openTab("Sliders");
        compareScreen("sliders");

        // Uploads tab
        themeTabSheet.openTab("Uploads");
        compareScreen("uploads");

        // Forms tab
        themeTabSheet.openTab("Forms");
        compareScreen("forms");

        // Tables tab
        themeTabSheet.openTab("Tables");
        testTables();

        // Trees tab
        themeTabSheet.openTab("Trees");
        compareScreen("trees");

        // TreeTable tab
        themeTabSheet.openTab("TreeTable");
        compareScreen("treetable");

        // Layouts tab
        themeTabSheet.openTab("Layouts");
        compareScreen("layouts");

        // TabSheets tab
        themeTabSheet.openTab("TabSheets");
        compareScreen("tabsheets");

        // Accordions tab
        themeTabSheet.openTab("Accordions");
        compareScreen("accordions");

        // Windows tab
        themeTabSheet.openTab("Windows");
        testWindows();

        // Notifications tab
        themeTabSheet.openTab("Notifications");
        testNotifications();
    }

    private void testNotifications() throws IOException {
        testNotification(0, "notification-humanized");
        testNotification(1, "notification-warning");
        testNotification(2, "notification-error");
        testNotification(3, "notification-tray");
    }

    private void testNotification(int id, String identifier) throws IOException {
        $(ButtonElement.class).id("notifButt" + id).click();
        compareScreen(identifier);
        $(FixedNotificationElement.class).first().close();
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
            WebElement textBox = searchComboBox.findElement(By
                    .vaadin("#textbox"));
            textBox.click();
            textBox.sendKeys(Keys.ARROW_DOWN);
        }
        compareScreen("selects-fourth-open");

        $(ComboBoxElement.class).id("select8").openPopup();
        compareScreen("selects-fifth-open");
    }

    private void testDates() throws IOException {
        compareScreen("dates");
        $(DateFieldElement.class).id("datefield0").openPopup();
        compareScreen("dates-first-popup");
        $(DateFieldElement.class).id("datefield1").openPopup();
        compareScreen("dates-second-popup");
        $(DateFieldElement.class).id("datefield2").openPopup();
        compareScreen("dates-third-popup");
        $(DateFieldElement.class).id("datefield3").openPopup();
        compareScreen("dates-fourth-popup");
    }
}
