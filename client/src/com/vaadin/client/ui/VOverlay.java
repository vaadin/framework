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

import java.util.logging.Logger;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.Util;
import com.vaadin.client.widgets.Overlay;

/**
 * In Vaadin UI this VOverlay should always be used for all elements that
 * temporary float over other components like context menus etc. This is to deal
 * stacking order correctly with VWindow objects.
 * <p>
 * To use this correctly, use {@link GWT#create(Class)} to create the
 * {@link Overlay} superclass and the default widgetset will replace it with
 * this. The widget will not be dependent on this Vaadin specific widget and can
 * be used in a pure GWT environment.
 * 
 * @deprecated as this is specifically for Vaadin only, it should not be used
 *             directly.
 */
@Deprecated
public class VOverlay extends Overlay implements CloseHandler<PopupPanel> {

    /*
     * ApplicationConnection that this overlay belongs to, which is needed to
     * create the overlay in the correct container so that the correct styles
     * are applied. If not given, owner will be used to figure out, and as a
     * last fallback, the overlay is created w/o container, potentially missing
     * styles.
     */
    protected ApplicationConnection ac;

    public VOverlay() {
        super();
    }

    public VOverlay(boolean autoHide) {
        super(autoHide);
    }

    public VOverlay(boolean autoHide, boolean modal) {
        super(autoHide, modal);
    }

    /**
     * @deprecated See main JavaDoc for VOverlay. Use the other constructors
     *             without the <code>showShadow</code> parameter.
     */
    @Deprecated
    public VOverlay(boolean autoHide, boolean modal, boolean showShadow) {
        super(autoHide, modal, showShadow);
    }

    /*
     * A "thread local" of sorts, set temporarily so that VOverlayImpl knows
     * which VOverlay is using it, so that it can be attached to the correct
     * overlay container.
     * 
     * TODO this is a strange pattern that we should get rid of when possible.
     */
    protected static VOverlay current;

    /**
     * Get the {@link ApplicationConnection} that this overlay belongs to. If
     * it's not set, {@link #getOwner()} is used to figure it out.
     * 
     * @return
     */
    protected ApplicationConnection getApplicationConnection() {
        if (ac != null) {
            return ac;
        } else if (getOwner() != null) {
            ComponentConnector c = Util.findConnectorFor(getOwner());
            if (c != null) {
                ac = c.getConnection();
            }
            return ac;
        } else {
            return null;
        }
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
            // could not figure out which one we belong to, styling will
            // probably fail
            Logger.getLogger(getClass().getSimpleName())
                    .warning(
                            "Could not determine ApplicationConnection for Overlay. Overlay will be attached directly to the root panel");
            return super.getOverlayContainer();
        } else {
            return getOverlayContainer(ac);
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
        id = id += "-overlays";
        Element container = DOM.getElementById(id);
        if (container == null) {
            container = DOM.createDiv();
            container.setId(id);
            String styles = ac.getUIConnector().getWidget().getParent()
                    .getStyleName();
            if (styles != null && !styles.equals("")) {
                container.addClassName(styles);
            }
            container.addClassName(CLASSNAME_CONTAINER);
            RootPanel.get().getElement().appendChild(container);
        }
        return DOM.asOld(container);
    }

    /**
     * Set the label of the container element, where tooltip, notification and
     * dialgs are added to.
     * 
     * @param applicationConnection
     *            the application connection for which to change the label
     * @param overlayContainerLabel
     *            label for the container
     */
    public static void setOverlayContainerLabel(
            ApplicationConnection applicationConnection,
            String overlayContainerLabel) {
        Roles.getAlertRole().setAriaLabelProperty(
                VOverlay.getOverlayContainer(applicationConnection),
                overlayContainerLabel);
    }

}