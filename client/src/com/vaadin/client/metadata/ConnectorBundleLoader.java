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
package com.vaadin.client.metadata;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.FastStringMap;
import com.vaadin.client.metadata.AsyncBundleLoader.State;
import com.vaadin.client.ui.VNotification;
import com.vaadin.shared.Position;

public abstract class ConnectorBundleLoader {

    public static class CValUiInfo {
        public final String widgetset;
        public final String product;
        public final String version;
        public final String type;

        public CValUiInfo(String product, String version, String widgetset,
                String type) {
            this.product = product;
            this.version = version;
            this.widgetset = widgetset;
            this.type = type;
        }
    }

    public static final String EAGER_BUNDLE_NAME = "__eager";
    public static final String DEFERRED_BUNDLE_NAME = "__deferred";

    private static ConnectorBundleLoader impl;

    private FastStringMap<AsyncBundleLoader> asyncBlockLoaders = FastStringMap
            .create();
    private FastStringMap<String> identifierToBundle = FastStringMap.create();

    private final TypeDataStore datStore = new TypeDataStore();

    public ConnectorBundleLoader() {
        init();
    }

    public TypeDataStore getTypeDataStore() {
        return datStore;
    }

    public static ConnectorBundleLoader get() {
        if (impl == null) {
            impl = GWT.create(ConnectorBundleLoader.class);
        }
        return impl;
    }

    public void loadBundle(String packageName, BundleLoadCallback callback) {
        AsyncBundleLoader loader = asyncBlockLoaders.get(packageName);
        switch (loader.getState()) {
        case NOT_STARTED:
            loader.load(callback, getTypeDataStore());
            break;
        case LOADING:
            loader.addCallback(callback);
            break;
        case LOADED:
            if (callback != null) {
                callback.loaded();
            }
            break;
        case ERROR:
            if (callback != null) {
                callback.failed(loader.getError());
            }
        }
    }

    public boolean isBundleLoaded(String bundleName) {
        AsyncBundleLoader loader = asyncBlockLoaders.get(bundleName);
        if (loader == null) {
            throw new IllegalArgumentException("Bundle " + bundleName
                    + " not recognized");
        }
        return loader.getState() == State.LOADED;
    }

    public void setLoaded(String packageName) {
        List<BundleLoadCallback> callbacks = asyncBlockLoaders.get(packageName)
                .setLoaded();
        for (BundleLoadCallback callback : callbacks) {
            if (callback != null) {
                callback.loaded();
            }
        }
    }

    public void setLoadFailure(String bundleName, Throwable reason) {
        reason = new RuntimeException("Failed to load bundle " + bundleName
                + ": " + reason.getMessage(), reason);
        List<BundleLoadCallback> callbacks = asyncBlockLoaders.get(bundleName)
                .setError(reason);
        for (BundleLoadCallback callback : callbacks) {
            callback.failed(reason);
        }
    }

    public String getBundleForIdentifier(String identifier) {
        return identifierToBundle.get(identifier);
    }

    protected void addAsyncBlockLoader(AsyncBundleLoader loader) {
        String name = loader.getName();
        asyncBlockLoaders.put(name, loader);
        String[] indentifiers = loader.getIndentifiers();
        for (String identifier : indentifiers) {
            identifierToBundle.put(identifier, name);
        }
    }

    public abstract void init();

    protected List<CValUiInfo> cvals = new ArrayList<CValUiInfo>();

    public void cval(String typeName) {
        if (!cvals.isEmpty()) {
            String msg = "";
            for (CValUiInfo c : cvals) {
                String ns = c.widgetset.replaceFirst("\\.[^\\.]+$", "");
                if (typeName.startsWith(ns)) {
                    cvals.remove(c);
                    msg += c.product + " " + c.version + "<br/>";
                }
            }
            if (!msg.isEmpty()) {
                // We need a widget for using VNotification, using the
                // context-menu parent. Is there an easy way?
                Widget w = ApplicationConfiguration.getRunningApplications()
                        .get(0).getContextMenu().getParent();
                VNotification n = VNotification.createNotification(0, w);
                n.setWidget(new HTML("Using Evaluation License of:<br/>" + msg));
                n.show(Position.BOTTOM_RIGHT);
            }
        }
    }
}
