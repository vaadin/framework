package com.example;

import org.junit.Ignore;
import org.junit.Test;

public class VaadinSpringBootPushStateNavigatorIT
        extends VaadinSpringBootURIFragmentNavigatorIT {

    @Ignore("PushState navigation is partially broken with Spring.")
    @Override
    @Test
    public void testNotDefaultView() {
        super.testNotDefaultView();
    }

    @Override
    protected String getPath() {
        return "subpath";
    }

    @Override
    protected String getViewSeparator() {
        return "/";
    }
}
