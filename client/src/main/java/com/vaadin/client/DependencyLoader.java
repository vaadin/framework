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

import com.google.gwt.core.client.JsArray;
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
 * @since 8.0
 */
public class DependencyLoader {

    private static final String DEPENDENCIES = "dependencies";
    private ApplicationConnection connection = null;

    private ResourceLoader loader = ResourceLoader.get();

    private ResourceLoadListener dependencyLoadingTracker = new ResourceLoadListener() {

        @Override
        public void onLoad(ResourceLoadEvent event) {
            ApplicationConfiguration.endDependencyLoading();
        }

        @Override
        public void onError(ResourceLoadEvent event) {
            String error = event.getResourceUrl() + " could not be loaded.";
            if (event.getResourceUrl().endsWith("css")) {
                error += " or the load detection failed because the stylesheet is empty.";
            }
            getLogger().severe(error);
            // The show must go on
            onLoad(event);
        }

    };

    /**
     * Sets the ApplicationConnection this instance is connected to.
     *
     * Only used internally.
     *
     * @param connection
     *            The ApplicationConnection for this instance
     */
    public void setConnection(ApplicationConnection connection) {
        if (this.connection != null) {
            throw new IllegalStateException(
                    "Application connection has already been set");
        }
        if (connection == null) {
            throw new IllegalArgumentException(
                    "ApplicationConnection can not be null");
        }
        this.connection = connection;
    }

    /**
     * Loads the any dependencies present in the given json snippet.
     *
     * Handles all dependencies found with the key "{@literal dependencies}".
     *
     * Ensures that
     * <ul>
     * <li>JavaScript dependencies are loaded in the given order.
     * <li>HTML imports are loaded after all JavaScripts are loaded and
     * executed.
     * <li>Style sheets are loaded and evaluated in some undefined order
     * </ul>
     *
     * @param json
     *            the JSON containing the dependencies to load
     */
    public void loadDependencies(ValueMap json) {
        if (!json.containsKey(DEPENDENCIES)) {
            return;
        }
        JsArray<ValueMap> deps = json.getJSValueMapArray(DEPENDENCIES);

        for (int i = 0; i < deps.length(); i++) {
            ValueMap dep = deps.get(i);
            String type = dep.getAsString("type");
            String url = connection.translateVaadinUri(dep.getAsString("url"));
            ApplicationConfiguration.startDependencyLoading();
            if (type.equals("STYLESHEET")) {
                loader.loadStylesheet(url, dependencyLoadingTracker);
            } else if (type.equals("JAVASCRIPT")) {
                loader.loadScript(url, dependencyLoadingTracker);
            } else if (type.equals("HTMLIMPORT")) {
                loader.loadHtmlImport(url, dependencyLoadingTracker);
            } else {
                ApplicationConfiguration.endDependencyLoading();
                throw new IllegalArgumentException("Unknown type: " + type);
            }
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(DependencyLoader.class.getName());
    }

}
