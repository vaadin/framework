package com.vaadin.tests.rpclogger;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public class RPCLoggerServlet extends VaadinServlet {

    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        RPCLoggerService service = new RPCLoggerService(this,
                deploymentConfiguration);
        service.init();
        return service;
    }
}
