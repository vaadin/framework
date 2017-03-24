package com.vaadin.osgi.compatibility.widgetset;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;

import com.vaadin.osgi.resources.OSGiVaadinResources;
import com.vaadin.osgi.resources.VaadinResourceService;

@Component(immediate = true)
public class CompatibilityWidgetsetContribution {
    private HttpService httpService;

    private static final String WIDGETSET_NAME = "com.vaadin.v7.Vaadin7WidgetSet";

    @Activate
    void startup(ComponentContext context) throws Exception {
        VaadinResourceService service = OSGiVaadinResources.getService();
        service.publishWidgetset(WIDGETSET_NAME, httpService);
    }

    @Reference
    void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }
}
