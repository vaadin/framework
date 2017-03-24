package com.vaadin.osgi.compatibility.themes;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;

import com.vaadin.osgi.resources.OSGiVaadinResources;
import com.vaadin.osgi.resources.VaadinResourceService;

@Component(immediate = true)
public class LegacyThemeContributions {
    private static final String[] LEGACY_THEMES = { "base", "chameleon",
            "reindeer", "runo" };

    private HttpService httpService;

    @Activate
    void startup() throws Exception {
        VaadinResourceService service = OSGiVaadinResources.getService();
        for (String themeName : LEGACY_THEMES) {
            service.publishTheme(themeName, httpService);
        }
    }

    @Reference
    void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }
}
