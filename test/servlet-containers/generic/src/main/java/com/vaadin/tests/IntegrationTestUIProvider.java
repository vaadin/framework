package com.vaadin.tests;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.integration.ServletIntegrationUI;
import com.vaadin.tests.integration.push.BasicPush;
import com.vaadin.ui.UI;

public class IntegrationTestUIProvider extends UIProvider {

    public static final String[] defaultPackages = {
            "com.vaadin.tests.integration",
            "com.vaadin.tests.integration.push"};

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
            e.printStackTrace();
            return null;
        }
    }

    @WebServlet(urlPatterns = {"/run/*", "/VAADIN/*"}, name = "IntegrationTestUIProvider", asyncSupported = true, initParams = {
            @WebInitParam(name = "UIProvider", value = "com.vaadin.tests.IntegrationTestUIProvider")})
    @VaadinServletConfiguration(ui = ServletIntegrationUI.class, productionMode = false)
    public static class MyServlet extends VaadinServlet {
    }

    @WebServlet(urlPatterns = "/run-jsr356/*", name = "IntegrationUIProvider-Jsr356", asyncSupported = false,
            initParams = {
                    @WebInitParam(name = "UIProvider", value = "com.vaadin.tests.IntegrationTestUIProvider"),
                    @WebInitParam(name = "org.atmosphere.cpr.asyncSupport", value = "org.atmosphere.container.JSR356AsyncSupport")
            })
    @VaadinServletConfiguration(ui = ServletIntegrationUI.class, productionMode = false)
    public static class JSR356Servlet extends VaadinServlet {
    }
}
