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

package com.vaadin.server;

import java.util.Collections;
import java.util.Set;

import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.ui.BrowserWindowOpenerState;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.UI;

/**
 * Component extension that opens a browser popup window when the extended
 * component is clicked.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class BrowserWindowOpener extends AbstractExtension {

    private static class BrowserWindowOpenerUIProvider extends UIProvider {

        private final String path;
        private final Class<? extends UI> uiClass;

        public BrowserWindowOpenerUIProvider(Class<? extends UI> uiClass,
                String path) {
            this.path = ensureInitialSlash(path);
            this.uiClass = uiClass;
        }

        private static String ensureInitialSlash(String path) {
            if (path == null) {
                return null;
            } else if (!path.startsWith("/")) {
                return '/' + path;
            } else {
                return path;
            }
        }

        @Override
        public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
            String requestPathInfo = event.getRequest().getPathInfo();
            if (path.equals(requestPathInfo)) {
                return uiClass;
            } else {
                return null;
            }
        }
    }

    private final BrowserWindowOpenerUIProvider uiProvider;

    /**
     * Creates a window opener that will open windows containing the provided UI
     * class
     * 
     * @param uiClass
     *            the UI class that should be opened when the extended component
     *            is clicked
     */
    public BrowserWindowOpener(Class<? extends UI> uiClass) {
        this(uiClass, generateUIClassUrl(uiClass));
    }

    /**
     * Creates a window opener that will open windows containing the provided UI
     * using the provided path
     * 
     * @param uiClass
     *            the UI class that should be opened when the extended component
     *            is clicked
     * @param path
     *            the path that the UI should be bound to
     */
    public BrowserWindowOpener(Class<? extends UI> uiClass, String path) {
        // Create a Resource with a translated URL going to the VaadinService
        this(new ExternalResource(ApplicationConstants.APP_PROTOCOL_PREFIX
                + path), new BrowserWindowOpenerUIProvider(uiClass, path));
    }

    /**
     * Creates a window opener that will open windows to the provided URL
     * 
     * @param url
     *            the URL to open in the window
     */
    public BrowserWindowOpener(String url) {
        this(new ExternalResource(url));
    }

    /**
     * Creates a window opener that will open window to the provided resource
     * 
     * @param resource
     *            the resource to open in the window
     */
    public BrowserWindowOpener(Resource resource) {
        this(resource, null);
    }

    private BrowserWindowOpener(Resource resource,
            BrowserWindowOpenerUIProvider uiProvider) {
        this.uiProvider = uiProvider;
        setResource(BrowserWindowOpenerState.locationResource, resource);
    }

    public void extend(AbstractComponent target) {
        super.extend(target);
    }

    /**
     * Sets the target window name that will be used. If a window has already
     * been opened with the same name, the contents of that window will be
     * replaced instead of opening a new window. If the name is
     * <code>null</code> or <code>"_blank"</code>, a new window will always be
     * opened.
     * 
     * @param windowName
     *            the target name for the window
     */
    public void setWindowName(String windowName) {
        getState().target = windowName;
    }

    /**
     * Gets the target window name.
     * 
     * @see #setWindowName(String)
     * 
     * @return the window target string
     */
    public String getWindowName() {
        return getState(false).target;
    }

    // Avoid breaking url to multiple lines
    // @formatter:off 
    /**
     * Sets the features for opening the window. See e.g.
     * {@link https://developer.mozilla.org/en-US/docs/DOM/window.open#Position_and_size_features}
     * for a description of the commonly supported features.
     * 
     * @param features a string with window features, or <code>null</code> to use the default features.
     */
    // @formatter:on
    public void setFeatures(String features) {
        getState().features = features;
    }

    /**
     * Gets the window features.
     * 
     * @see #setFeatures(String)
     * @return
     */
    public String getFeatures() {
        return getState(false).features;
    }

    @Override
    protected BrowserWindowOpenerState getState() {
        return (BrowserWindowOpenerState) super.getState();
    }

    @Override
    protected BrowserWindowOpenerState getState(boolean markAsDirty) {
        return (BrowserWindowOpenerState) super.getState(markAsDirty);
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
        return "popup/" + uiClass.getSimpleName();
    }

    /**
     * Sets a URI fragment that will be added to the URI opened in the window.
     * If the window is opened to contain a Vaadin UI, the fragment will be
     * available using {@link Page#getUriFragment()} on the Page instance of the
     * new UI.
     * <p>
     * The default value is <code>null</code>.
     * 
     * @param uriFragment
     *            the URI fragment string that should be included in the opened
     *            URI, or <code>null</code> to preserve the original fragment of
     *            the URI.
     */
    public void setUriFragment(String uriFragment) {
        getState().uriFragment = uriFragment;
    }

    /**
     * Gets that URI fragment configured for opened windows.
     * 
     * @return the URI fragment string, or <code>null</code> if no fragment is
     *         configured.
     * 
     * @see #setUriFragment(String)
     */
    public String getUriFragment() {
        return getState(false).uriFragment;
    }

    /**
     * Sets a parameter that will be added to the query string of the opened
     * URI. If the window is opened to contain a Vaadin UI, the parameter will
     * be available using {@link VaadinRequest#getParameter(String)} e.g. using
     * the request instance passed to {@link UI#init(VaadinRequest)}.
     * <p>
     * Setting a parameter with the same name as a previously set parameter will
     * replace the previous value.
     * 
     * @param name
     *            the name of the parameter to add, not <code>null</code>
     * @param value
     *            the value of the parameter to add, not <code>null</code>
     * 
     * @see #removeParameter(String)
     * @see #getParameterNames()
     * @see #getParameter(String)
     */
    public void setParameter(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException("Null not allowed");
        }
        getState().parameters.put(name, value);
    }

    /**
     * Removes a parameter that has been set using
     * {@link #setParameter(String, String)}. Removing a parameter that has not
     * been set has no effect.
     * 
     * @param name
     *            the name of the parameter to remove, not <code>null</code>
     * 
     * @see #setParameter(String, String)
     */
    public void removeParameter(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null not allowed");
        }
        getState().parameters.remove(name);
    }

    /**
     * Gets the names of all parameters set using
     * {@link #setParameter(String, String)}.
     * 
     * @return an unmodifiable set of parameter names
     * 
     * @see #setParameter(String, String)
     * @see #getParameter(String)
     */
    public Set<String> getParameterNames() {
        return Collections.unmodifiableSet(getState().parameters.keySet());
    }

    /**
     * Gets the value of a parameter set using
     * {@link #setParameter(String, String)}. If there is no parameter with the
     * given name, <code>null</code> is returned.
     * 
     * @param name
     *            the name of the parameter to get, not <code>null</code>
     * @return the value of the parameter, or <code>null</code> there is no
     *         parameter
     * 
     * @see #setParameter(String, String)
     * @see #getParameter(String)
     */
    public String getParameter(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null not allowed");
        }
        return getState(false).parameters.get(name);
    }

}
