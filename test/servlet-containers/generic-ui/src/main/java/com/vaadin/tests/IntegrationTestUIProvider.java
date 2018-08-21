package com.vaadin.tests;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.tests.integration.push.BasicPush;
import com.vaadin.ui.UI;

public class IntegrationTestUIProvider extends UIProvider {

    public static final String[] defaultPackages = {
            "com.vaadin.tests.integration",
            "com.vaadin.tests.integration.push" };

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        Class<? extends UI> uiClass = findUIClassFromPath(event);
        return uiClass != null ? uiClass : BasicPush.class;
    }

    private Class<? extends UI> findUIClassFromPath(
            UIClassSelectionEvent event) {
        String pathInfo = event.getRequest().getPathInfo();
        if (pathInfo != null) {
            String className = pathInfo.substring(1);
            if (className.startsWith("run/")) {
                className = className.substring(4);
            }

            if (className.contains(".")) {
                return getUIClass(className);
            } else {
                return getUIClassFromDefaultPackage(className);
            }
        }
        return null;
    }

    private Class<? extends UI> getUIClassFromDefaultPackage(String className) {
        for (String pkgName : defaultPackages) {
            Class<? extends UI> uiClass = getUIClass(pkgName + "." + className);
            if (uiClass != null) {
                return uiClass;
            }
        }
        return null;
    }

    private Class<? extends UI> getUIClass(String className) {
        try {
            Class<?> loadClass = getClass().getClassLoader()
                    .loadClass(className.replace("/", "."));
            return (Class<? extends UI>) loadClass;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
