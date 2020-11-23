/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.server;

import java.util.EventObject;

/**
 * Event fired to {@link ServiceDestroyListener} when a {@link VaadinService} is
 * being destroyed.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class ServiceDestroyEvent extends EventObject {

    /**
     * Creates a new event for the given service.
     *
     * @param service
     *            the service being destroyed
     */
    public ServiceDestroyEvent(VaadinService service) {
        super(service);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.EventObject#getSource()
     */
    @Override
    public VaadinService getSource() {
        return (VaadinService) super.getSource();
    }

}
