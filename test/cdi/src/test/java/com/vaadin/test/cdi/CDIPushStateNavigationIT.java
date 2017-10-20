package com.vaadin.test.cdi;

import org.junit.Ignore;
import org.junit.Test;

public class CDIPushStateNavigationIT extends CDINavigationIT {

    protected String getUIPath() {
        return "/subpath";
    }

    @Override
    protected String getViewSeparator() {
        return "/";
    }

    @Ignore("Something goes wrong with the navigation between UIs, navigator not sufficiently reset.")
    @Test
    public void testNavigateFromSubPathToRootPath() {
        navigateTo("new");

        // Navigate to root
        getDriver().get(BASE_URL + "/");
        navigateTo("new", "/", "#!");
    }

    @Ignore("PushStateNavigation does not register URLs properly")
    @Test
    @Override
    public void testReloadPage() {
        super.testReloadPage();
    }
}
