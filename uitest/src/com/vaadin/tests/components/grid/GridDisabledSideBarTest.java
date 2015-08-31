package com.vaadin.tests.components.grid;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridDisabledSideBarTest extends GridBasicClientFeaturesTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    private void makeColumnHidable() {
        selectMenuPath("Component", "Columns", "Column 0", "Hidable");
    }

    private void toggleSideBarMenuAndDisable() {
        selectMenuPath("Component", "Sidebar", "Open sidebar and disable grid");
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return !findElement(By.className("v-grid-sidebar-button")).isEnabled();
            }
        });
    }
    private void clickSideBarButton() {
        findElement(By.cssSelector(".v-grid-sidebar-button")).click();
    }

    private void toggleEnabled() {
        selectMenuPath("Component", "State", "Enabled");
    }

    private void assertSideBarContainsClass(String cssClass) {
        assertThat(findElement(By.cssSelector(".v-grid-sidebar")).getAttribute("class"), containsString(cssClass));
    }

    @Test
    public void sidebarButtonIsDisabledOnCreation() {
        selectMenuPath("Component", "State", "Enabled");
        makeColumnHidable();

        clickSideBarButton();

        assertSideBarContainsClass("closed");
    }

    @Test
    public void sidebarButtonCanBeEnabled() {
        makeColumnHidable();

        clickSideBarButton();

        assertSideBarContainsClass("open");
    }

    @Test
    public void sidebarButtonCanBeDisabled() {
        makeColumnHidable();
        toggleEnabled();

        clickSideBarButton();

        assertSideBarContainsClass("closed");
    }

    @Test
    public void sidebarIsClosedOnDisable() {
        makeColumnHidable();

        toggleSideBarMenuAndDisable();

        assertSideBarContainsClass("closed");
    }
}