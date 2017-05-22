package com.vaadin.test.dependencyrewrite;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.DependencyFilter;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public class MyService extends VaadinServletService {

    public MyService(VaadinServlet servlet,
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        super(servlet, deploymentConfiguration);
    }

    @Override
    protected List<DependencyFilter> initDependencyFilters(
            List<DependencyFilter> sessionInitFilters) throws ServiceException {
        List<DependencyFilter> list = new ArrayList<>(
                super.initDependencyFilters(sessionInitFilters));
        list.add(new ApplicationDependencyFilter());
        return list;
    }

}
