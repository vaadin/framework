/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.client;

import com.google.gwt.user.client.ui.FlowPanel;

public class VCaptionWrapper extends FlowPanel {

    public static final String CLASSNAME = "v-captionwrapper";
    VCaption caption;
    ComponentConnector wrappedConnector;

    /**
     * Creates a new caption wrapper panel.
     *
     * @param toBeWrapped
     *            paintable that the caption is associated with, not null
     * @param client
     *            ApplicationConnection
     */
    public VCaptionWrapper(ComponentConnector toBeWrapped,
            ApplicationConnection client) {
        caption = new VCaption(toBeWrapped, client);
        add(caption);
        wrappedConnector = toBeWrapped;
        add(wrappedConnector.getWidget());
        setStyleName(CLASSNAME);
    }

    public void updateCaption() {
        caption.updateCaption();
    }

    public ComponentConnector getWrappedConnector() {
        return wrappedConnector;
    }
}
