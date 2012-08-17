/* 
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.terminal.gwt.server;

import com.vaadin.Application;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

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

    public static ResourceReference create(Resource resource) {
        if (resource == null) {
            return null;
        } else {
            return new ResourceReference(resource);
        }
    }

    public static Resource getResource(URLReference reference) {
        if (reference == null) {
            return null;
        }
        assert reference instanceof ResourceReference;
        return ((ResourceReference) reference).getResource();
    }
}
