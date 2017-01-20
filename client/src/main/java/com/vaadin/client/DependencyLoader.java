/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import java.util.logging.Logger;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Command;
import com.vaadin.client.ResourceLoader.ResourceLoadEvent;
import com.vaadin.client.ResourceLoader.ResourceLoadListener;

/**
 * Handles loading of dependencies (style sheets and scripts) in the
 * application.
 *
 * Use {@link ApplicationConfiguration#runWhenDependenciesLoaded(Command)} to
 * execute a command after all dependencies have finished loading.
 *
 * @author Vaadin Ltd
 * @since 8.0.0
 */
public class DependencyLoader {

    private static final String STYLE_DEPENDENCIES = "styleDependencies";
    private static final String SCRIPT_DEPENDENCIES = "scriptDependencies";
    private ApplicationConnection connection;

    /**
     * Sets the ApplicationConnection this instance is connected to.
     *
     * Only used internally.
     *
     * @param connection
     *            The ApplicationConnection for this instance
     */
    public void setConnection(ApplicationConnection connection) {
        this.connection = connection;
    }

    /**
     * Loads the any dependencies present in the given json snippet.
     *
     * Scans the key "{@literal scriptDependencies}" for JavaScripts and the key
     * "{@literal styleDependencies}" for style sheets.
     *
     * Ensures that the given JavaScript dependencies are loaded in the given
     * order. Does not ensure anything about stylesheet order.
     *
     * @param json
     *            the JSON containing the dependencies to load
     */
    public void loadDependencies(ValueMap json) {
        if (json.containsKey(SCRIPT_DEPENDENCIES)) {
            loadScriptDependencies(json.getJSStringArray(SCRIPT_DEPENDENCIES));
        }
        if (json.containsKey(STYLE_DEPENDENCIES)) {
            loadStyleDependencies(json.getJSStringArray(STYLE_DEPENDENCIES));
        }

    }

    private void loadStyleDependencies(JsArrayString dependencies) {
        // Assuming no reason to interpret in a defined order
        ResourceLoadListener resourceLoadListener = new ResourceLoadListener() {
            @Override
            public void onLoad(ResourceLoadEvent event) {
                ApplicationConfiguration.endDependencyLoading();
            }

            @Override
            public void onError(ResourceLoadEvent event) {
                getLogger().severe(event.getResourceUrl()
                        + " could not be loaded, or the load detection failed because the stylesheet is empty.");
                // The show must go on
                onLoad(event);
            }
        };
        ResourceLoader loader = ResourceLoader.get();
        for (int i = 0; i < dependencies.length(); i++) {
            String url = translateVaadinUri(dependencies.get(i));
            ApplicationConfiguration.startDependencyLoading();
            loader.loadStylesheet(url, resourceLoadListener);
        }
    }

    private void loadScriptDependencies(final JsArrayString dependencies) {
        if (dependencies.length() == 0) {
            return;
        }

        // Listener that loads the next when one is completed
        ResourceLoadListener resourceLoadListener = new ResourceLoadListener() {
            @Override
            public void onLoad(ResourceLoadEvent event) {
                if (dependencies.length() != 0) {
                    String url = translateVaadinUri(dependencies.shift());
                    ApplicationConfiguration.startDependencyLoading();
                    // Load next in chain (hopefully already preloaded)
                    event.getResourceLoader().loadScript(url, this);
                }
                // Call start for next before calling end for current
                ApplicationConfiguration.endDependencyLoading();
            }

            @Override
            public void onError(ResourceLoadEvent event) {
                getLogger().severe(
                        event.getResourceUrl() + " could not be loaded.");
                // The show must go on
                onLoad(event);
            }
        };

        ResourceLoader loader = ResourceLoader.get();

        // Start chain by loading first
        String url = translateVaadinUri(dependencies.shift());
        ApplicationConfiguration.startDependencyLoading();
        loader.loadScript(url, resourceLoadListener);

        for (int i = 0; i < dependencies.length(); i++) {
            String preloadUrl = translateVaadinUri(dependencies.get(i));
            loader.loadScript(preloadUrl, null);
        }
    }

    private String translateVaadinUri(String url) {
        return connection.translateVaadinUri(url);
    }

    private static Logger getLogger() {
        return Logger.getLogger(DependencyLoader.class.getName());
    }

}