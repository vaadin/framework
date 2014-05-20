/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.vaadin.client.ApplicationConnection;

public class VWindowOverlay extends VOverlay {
    public VWindowOverlay() {
    }

    public VWindowOverlay(boolean autoHide, boolean modal, boolean showShadow) {
        super(autoHide, modal, showShadow);
    }

    /**
     * Gets the 'overlay container' element. Tries to find the current
     * {@link ApplicationConnection} using {@link #getApplicationConnection()}.
     * 
     * @return the overlay container element for the current
     *         {@link ApplicationConnection} or another element if the current
     *         {@link ApplicationConnection} cannot be determined.
     */
    @Override
    public com.google.gwt.user.client.Element getOverlayContainer() {
        ApplicationConnection ac = getApplicationConnection();
        if (ac == null) {
            return super.getOverlayContainer();
        } else {
            Element overlayContainer = getOverlayContainer(ac);
            return DOM.asOld(overlayContainer);
        }
    }

    /**
     * Gets the 'overlay container' element pertaining to the given
     * {@link ApplicationConnection}. Each overlay should be created in a
     * overlay container element, so that the correct theme and styles can be
     * applied.
     * 
     * @param ac
     *            A reference to {@link ApplicationConnection}
     * @return The overlay container
     */
    public static com.google.gwt.user.client.Element getOverlayContainer(
            ApplicationConnection ac) {
        String id = ac.getConfiguration().getRootPanelId();
        id = id += "-window-overlays";
        Element container = DOM.getElementById(id);
        if (container == null) {
            container = DOM.createDiv();
            container.setId(id);
            String styles = ac.getUIConnector().getWidget().getParent()
                    .getStyleName();
            container.addClassName(styles);
            container.addClassName(CLASSNAME_CONTAINER);
            RootPanel.get().getElement().appendChild(container);
        }

        return DOM.asOld(container);
    }
}
