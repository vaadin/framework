/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import com.vaadin.Application;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.communication.URLReference;

public class ResourceReference extends URLReference {

    private Resource resource;

    public ResourceReference(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    @Override
    public String getURL() {
        if (resource instanceof ExternalResource) {
            return ((ExternalResource) resource).getURL();
        } else if (resource instanceof ApplicationResource) {
            final ApplicationResource r = (ApplicationResource) resource;
            final Application a = r.getApplication();
            if (a == null) {
                throw new RuntimeException(
                        "An ApplicationResource ("
                                + r.getClass().getName()
                                + " must be attached to an application when it is sent to the client.");
            }
            final String uri = a.getRelativeLocation(r);
            return uri;
        } else if (resource instanceof ThemeResource) {
            final String uri = "theme://"
                    + ((ThemeResource) resource).getResourceId();
            return uri;
        } else {
            throw new RuntimeException(getClass().getSimpleName()
                    + " does not support resources of type: "
                    + resource.getClass().getName());
        }

    }
}
