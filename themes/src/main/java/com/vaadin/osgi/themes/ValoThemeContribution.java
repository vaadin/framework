package com.vaadin.osgi.themes;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;

import com.vaadin.osgi.resources.OSGiVaadinResources;
import com.vaadin.osgi.resources.VaadinResourceService;

@Component(immediate = true)
public class ValoThemeContribution {

    private HttpService httpService;

    @Activate
    void startup() throws Exception {
        VaadinResourceService service = OSGiVaadinResources.getService();
        service.publishTheme("valo", httpService);
    }

    @Reference
    void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }
}
