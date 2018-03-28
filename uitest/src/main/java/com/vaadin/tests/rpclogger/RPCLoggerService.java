package com.vaadin.tests.rpclogger;

import java.util.List;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public class RPCLoggerService extends VaadinServletService {

    public RPCLoggerService(VaadinServlet servlet,
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        super(servlet, deploymentConfiguration);
    }

    @Override
    protected List<RequestHandler> createRequestHandlers()
            throws ServiceException {
        List<RequestHandler> handlers = super.createRequestHandlers();
        handlers.add(new LoggingUidlRequestHandler());
        return handlers;
    }
}
