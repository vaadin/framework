package com.vaadin.test.dependencyrewrite;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;;

@WebServlet(urlPatterns = {
        "/*" }, name = "MyUIServlet", asyncSupported = true, initParams = @WebInitParam(name = "UIProvider", value = "com.vaadin.test.dependencyrewrite.MyUIProvider"))
public class MyUIServlet extends VaadinServlet {
    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        MyService service = new MyService(this, deploymentConfiguration);
        service.init();
        return service;
    }
}
