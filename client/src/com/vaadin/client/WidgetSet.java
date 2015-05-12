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

package com.vaadin.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.communication.HasJavaScriptConnectorHelper;
import com.vaadin.client.metadata.BundleLoadCallback;
import com.vaadin.client.metadata.ConnectorBundleLoader;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.client.ui.UnknownComponentConnector;

public class WidgetSet {
    /**
     * Create an uninitialized connector that best matches given UIDL. The
     * connector must implement {@link ServerConnector}.
     * 
     * @param tag
     *            connector type tag for the connector to create
     * @param conf
     *            the application configuration to use when creating the
     *            connector
     * 
     * @return New uninitialized and unregistered connector that can paint given
     *         UIDL.
     */
    public ServerConnector createConnector(int tag,
            ApplicationConfiguration conf) {
        /*
         * Yes, this (including the generated code in WidgetMap) may look very
         * odd code, but due the nature of GWT, we cannot do this any cleaner.
         * Luckily this is mostly written by WidgetSetGenerator, here are just
         * some hacks. Extra instantiation code is needed if client side
         * connector has no "native" counterpart on client side.
         */
        Profiler.enter("WidgetSet.createConnector");

        Class<? extends ServerConnector> classType = resolveInheritedConnectorType(
                conf, tag);

        if (classType == null || classType == UnknownComponentConnector.class) {
            String serverSideName = conf.getUnknownServerClassNameByTag(tag);
            UnknownComponentConnector c = GWT
                    .create(UnknownComponentConnector.class);
            c.setServerSideClassName(serverSideName);
            Profiler.leave("WidgetSet.createConnector");
            return c;
        } else {
            /*
             * let the auto generated code instantiate this type
             */
            try {
                ServerConnector connector = (ServerConnector) TypeData.getType(
                        classType).createInstance();
                if (connector instanceof HasJavaScriptConnectorHelper) {
                    ((HasJavaScriptConnectorHelper) connector)
                            .getJavascriptConnectorHelper().setTag(tag);
                }
                Profiler.leave("WidgetSet.createConnector");
                return connector;
            } catch (NoDataException e) {
                Profiler.leave("WidgetSet.createConnector");
                throw new IllegalStateException(
                        "There is no information about "
                                + classType
                                + ". Did you remember to compile the right widgetset?",
                        e);
            }
        }
    }

    private Class<? extends ServerConnector> resolveInheritedConnectorType(
            ApplicationConfiguration conf, int tag) {
        Class<? extends ServerConnector> classType = null;
        Integer t = tag;
        do {
            classType = resolveConnectorType(t, conf);
            t = conf.getParentTag(t);
        } while (classType == null && t != null);
        return classType;
    }

    protected Class<? extends ServerConnector> resolveConnectorType(int tag,
            ApplicationConfiguration conf) {
        Class<? extends ServerConnector> connectorClass = conf
                .getConnectorClassByEncodedTag(tag);

        return connectorClass;
    }

    /**
     * Due its nature, GWT does not support dynamic classloading. To bypass this
     * limitation, widgetset must have function that returns Class by its fully
     * qualified name.
     * 
     * @param tag
     * @param applicationConfiguration
     * @return
     */
    public void ensureConnectorLoaded(int tag, ApplicationConfiguration conf) {
        ConnectorBundleLoader loader = ConnectorBundleLoader.get();
        String bundleName = null;
        Integer t = tag;
        do {
            String serverSideClassName = conf.getServerSideClassNameForTag(t);
            bundleName = loader.getBundleForIdentifier(serverSideClassName);

            t = conf.getParentTag(t);
        } while (bundleName == null && t != null);

        if (bundleName != null && !loader.isBundleLoaded(bundleName)) {
            ApplicationConfiguration.startDependencyLoading();
            loader.loadBundle(bundleName, new BundleLoadCallback() {
                @Override
                public void loaded() {
                    ApplicationConfiguration.endDependencyLoading();
                }

                @Override
                public void failed(Throwable reason) {
                    getLogger().log(Level.SEVERE, "Error loading bundle",
                            reason);
                    ApplicationConfiguration.endDependencyLoading();
                }
            });
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(WidgetSet.class.getName());
    }
}
