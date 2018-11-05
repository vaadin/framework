package com.example;

import org.junit.Ignore;
import org.junit.Test;

public class VaadinSpringBootPushStateNavigatorIT
        extends VaadinSpringBootURIFragmentNavigatorIT {

    @Override
    protected String getPath() {
        return "subpath";
    }

    @Override
    protected String getViewSeparator() {
        return "/";
    }
}
