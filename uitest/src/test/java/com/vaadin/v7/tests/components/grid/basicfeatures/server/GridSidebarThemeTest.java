package com.vaadin.v7.tests.components.grid.basicfeatures.server;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridSidebarFeatures;

public class GridSidebarThemeTest extends GridBasicFeaturesTest {

    @Test
    public void testValo() throws Exception {
        runTestSequence("valo");
    }

    @Test
    public void testValoDark() throws Exception {
        runTestSequence("tests-valo-dark");
    }

    @Override
    protected Class<?> getUIClass() {
        return GridSidebarFeatures.class;
    }

    private void runTestSequence(String theme) throws IOException {
        openTestURL("theme=" + theme);
        waitUntilLoadingIndicatorNotVisible();

        compareScreen(theme + "-SidebarClosed");
        getSidebarOpenButton().click();
        waitForElementPresent(By.className("v-grid-sidebar-content"));
        sleep(100); // wait for animations to finish

        compareScreen(theme + "-SidebarOpen");

        new Actions(getDriver()).moveToElement(getColumnHidingToggle(2), 5, 5)
                .perform();

        compareScreen(theme + "-OnMouseOverNotHiddenToggle");

        getColumnHidingToggle(2).click();
        getColumnHidingToggle(3).click();
        getColumnHidingToggle(6).click();

        new Actions(getDriver()).moveToElement(getSidebarOpenButton())
                .perform();
        ;

        compareScreen(theme + "-TogglesTriggered");

        new Actions(getDriver()).moveToElement(getColumnHidingToggle(2))
                .perform();
        ;

        compareScreen(theme + "-OnMouseOverHiddenToggle");

        getSidebarOpenButton().click();
        waitForElementNotPresent(By.className("v-grid-sidebar-content"));

        compareScreen(theme + "-SidebarClosed2");
    }
}
