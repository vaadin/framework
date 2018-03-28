package com.vaadin.tests.applicationservlet;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.javascriptcomponent.BasicJavaScriptComponent;
import com.vaadin.ui.UI;

public class InitParamUIProvider extends UIProvider {

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        VaadinRequest request = event.getRequest();
        String pathInfo = request.getPathInfo();
        if ("/test".equals(pathInfo)) {
            return BasicJavaScriptComponent.class;
        } else {
            return null;
        }
    }

}
