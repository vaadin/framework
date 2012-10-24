/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.server;

import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.ui.BrowserPopupExtensionState;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.UI;

/**
 * Component extension that opens a browser popup window when the extended
 * component is clicked.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class BrowserPopupOpener extends AbstractExtension {

    private final BrowserPopupUIProvider uiProvider;

    /**
     * Creates a popup opener that will open popups containing the provided UI
     * class
     * 
     * @param uiClass
     *            the UI class that should be opened when the extended component
     *            is clicked
     */
    public BrowserPopupOpener(Class<? extends UI> uiClass) {
        this(uiClass, generateUIClassUrl(uiClass));
    }

    /**
     * Creates a popup opener that will open popups containing the provided UI
     * using the provided path
     * 
     * @param uiClass
     *            the UI class that should be opened when the extended component
     *            is clicked
     * @param path
     *            the path that the UI should be bound to
     */
    public BrowserPopupOpener(Class<? extends UI> uiClass, String path) {
        // Create a Resource with a translated URL going to the VaadinService
        this(new ExternalResource(ApplicationConstants.APP_PROTOCOL_PREFIX
                + path), new BrowserPopupUIProvider(uiClass, path));
    }

    /**
     * Creates a popup opener that will open popups to the provided URL
     * 
     * @param url
     *            the URL to open in the popup
     */
    public BrowserPopupOpener(String url) {
        this(new ExternalResource(url));
    }

    /**
     * Creates a popup opener that will open popups to the provided resource
     * 
     * @param resource
     *            the resource to open in the popup
     */
    public BrowserPopupOpener(Resource resource) {
        this(resource, null);
    }

    private BrowserPopupOpener(Resource resource,
            BrowserPopupUIProvider uiProvider) {
        this.uiProvider = uiProvider;
        setResource("popup", resource);
    }

    public void extend(AbstractComponent target) {
        super.extend(target);
    }

    /**
     * Sets the target window name that will be used when opening the popup. If
     * a popup has already been opened with the same name, the contents of that
     * window will be replaced instead of opening a new window. If the name is
     * <code>null</code> or <code>"blank"</code>, the popup will always be
     * opened in a new window.
     * 
     * @param popupName
     *            the target name for the popups
     */
    public void setPopupName(String popupName) {
        getState().target = popupName;
    }

    /**
     * Gets the popup target name.
     * 
     * @see #setPopupName(String)
     * 
     * @return the popup target string
     */
    public String getPopupName() {
        return getState().target;
    }

    // Avoid breaking url to multiple lines
    // @formatter:off 
    /**
     * Sets the features for opening the popup. See e.g.
     * {@link https://developer.mozilla.org/en-US/docs/DOM/window.open#Position_and_size_features}
     * for a description of the commonly supported features.
     * 
     * @param features a string with popup features, or <code>null</code> to use the default features.
     */
    // @formatter:on
    public void setFeatures(String features) {
        getState().features = features;
    }

    /**
     * Gets the popup features.
     * 
     * @see #setFeatures(String)
     * @return
     */
    public String getFeatres() {
        return getState().features;
    }

    @Override
    protected BrowserPopupExtensionState getState() {
        return (BrowserPopupExtensionState) super.getState();
    }

    @Override
    public void attach() {
        super.attach();
        if (uiProvider != null
                && !getSession().getUIProviders().contains(uiProvider)) {
            getSession().addUIProvider(uiProvider);
        }
    }

    @Override
    public void detach() {
        if (uiProvider != null) {
            getSession().removeUIProvider(uiProvider);
        }
        super.detach();
    }

    private static String generateUIClassUrl(Class<? extends UI> uiClass) {
        return uiClass.getSimpleName() + "-POPUP";
    }

}
