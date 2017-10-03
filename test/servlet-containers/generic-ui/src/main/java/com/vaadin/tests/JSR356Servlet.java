package com.vaadin.tests;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.integration.ServletIntegrationWebsocketUI;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * Created by elmot on 06-02-2017.
 */
@WebServlet(urlPatterns = "/run-jsr356/*", name = "IntegrationUIProvider-Jsr356", asyncSupported = false, initParams = {
        @WebInitParam(name = "org.atmosphere.cpr.asyncSupport", value = "org.atmosphere.container.JSR356AsyncSupport")})
@VaadinServletConfiguration(ui = ServletIntegrationWebsocketUI.class, productionMode = false)
public class JSR356Servlet extends VaadinServlet {

}
