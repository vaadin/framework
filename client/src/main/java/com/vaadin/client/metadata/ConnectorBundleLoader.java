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
package com.vaadin.client.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.vaadin.client.FastStringMap;
import com.vaadin.client.metadata.AsyncBundleLoader.State;

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
            throw new IllegalArgumentException(
                    "Bundle " + bundleName + " not recognized");
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

    protected List<CValUiInfo> cvals = new ArrayList<>();

    public void cval(String typeName) {
        if (!cvals.isEmpty()) {
            for (CValUiInfo c : cvals) {
                String ns = c.widgetset.replaceFirst("\\.[^\\.]+$", "");
                if (typeName.startsWith(ns)) {
                    notice(c.product + " " + c.version);
                    cvals.remove(c);
                    return;
                }
            }
        }
    }

    private HTML notice;

    // Not using Vaadin notifications (#14597)
    private void notice(String productName) {
        if (notice == null) {
            notice = new HTML();
            notice.addClickHandler(event -> notice.removeFromParent());
            notice.addTouchStartHandler(event -> notice.removeFromParent());
        }
        String msg = notice.getText().trim();
        msg += msg.isEmpty() ? "Using Evaluation License of: " : ", ";
        notice.setText(msg + productName);
        RootPanel.get().add(notice);

        notice.getElement().setClassName("");
        Style s = notice.getElement().getStyle();

        s.setPosition(Position.FIXED);
        s.setTextAlign(TextAlign.CENTER);
        s.setRight(0, Unit.PX);
        s.setLeft(0, Unit.PX);
        s.setBottom(0, Unit.PX);
        s.setProperty("padding", "0.5em 1em");

        s.setProperty("font-family", "sans-serif");
        s.setFontSize(12, Unit.PX);
        s.setLineHeight(1.1, Unit.EM);

        s.setColor("white");
        s.setBackgroundColor("black");
        s.setOpacity(0.7);

        s.setZIndex(2147483646);
        s.setProperty("top", "auto");
        s.setProperty("width", "auto");
        s.setDisplay(Display.BLOCK);
        s.setWhiteSpace(WhiteSpace.NORMAL);
        s.setVisibility(Visibility.VISIBLE);
        s.setMargin(0, Unit.PX);
    }

    /**
     * Starts loading the deferred bundle if it hasn't already been started.
     *
     * @since 8.0.3
     */
    public void ensureDeferredBundleLoaded() {
        if (!isBundleLoaded(DEFERRED_BUNDLE_NAME)) {
            loadBundle(DEFERRED_BUNDLE_NAME, new BundleLoadCallback() {
                @Override
                public void loaded() {
                    // Nothing to do
                }

                @Override
                public void failed(Throwable reason) {
                    getLogger().log(Level.SEVERE,
                            "Error loading deferred bundle", reason);
                }
            });
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(ConnectorBundleLoader.class.getName());
    }

    /**
     * Gets a list of all currently loaded bundle names.
     * <p>
     * This method is intended for testing the loading mechanism.
     *
     * @return a list of bundles, not <code>null</code>
     *
     * @since 8.0.3
     */
    public List<String> getLoadedBundles() {
        List<String> bundles = new ArrayList<>();

        JsArrayString keys = asyncBlockLoaders.getKeys();
        for (int i = 0; i < keys.length(); i++) {
            String bundleName = keys.get(i);
            if (isBundleLoaded(bundleName)) {
                bundles.add(bundleName);
            }
        }

        return bundles;
    }
}
