package com.vaadin.test.dependencyrewrite;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class MyUIProvider extends UIProvider {

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        String url = event.getRequest().getPathInfo();
        if (url.contains("/dynamic/")) {
            return DependencyDynamicUI.class;
        } else if (url.contains("/initial/")) {
            return DependencyInitialUI.class;
        } else {
            return null;
        }

    }

}
