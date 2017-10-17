package com.vaadin.v7.tests.components.grid;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

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
        waitUntil(input -> !findElement(By.className("v-grid-sidebar-button"))
                .isEnabled());
    }

    private void clickSideBarButton() {
        findElement(By.cssSelector(".v-grid-sidebar-button")).click();
    }

    private void toggleEnabled() {
        selectMenuPath("Component", "State", "Enabled");
    }

    private void assertSideBarContainsClass(String cssClass) {
        assertThat(findElement(By.cssSelector(".v-grid-sidebar"))
                .getAttribute("class"), containsString(cssClass));
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
