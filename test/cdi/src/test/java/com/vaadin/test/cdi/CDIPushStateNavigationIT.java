package com.vaadin.test.cdi;

public class CDIPushStateNavigationIT extends CDINavigationIT {

    protected String getUIPath() {
        return "/subpath";
    }

    @Override
    protected String getViewSeparator() {
        return "/";
    }
}
