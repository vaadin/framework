package com.vaadin.tests;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.integration.ServletIntegrationUI;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/*" ,name = "IntegrationTestUIProvider", asyncSupported = true, initParams = {
        @WebInitParam(name = "UIProvider", value = "com.vaadin.tests.IntegrationTestUIProvider")})
@VaadinServletConfiguration(ui = ServletIntegrationUI.class, productionMode = false)
public class ServerIntegrationTestServlet extends VaadinServlet {
}
