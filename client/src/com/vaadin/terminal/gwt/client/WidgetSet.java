/* 
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.communication.HasJavaScriptConnectorHelper;
import com.vaadin.terminal.gwt.client.ui.UnknownComponentConnector;

public class WidgetSet {

    /**
     * WidgetSet (and its extensions) delegate instantiation of widgets and
     * client-server matching to WidgetMap. The actual implementations are
     * generated with gwts generators/deferred binding.
     */
    private WidgetMap widgetMap = GWT.create(WidgetMap.class);

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

        Class<? extends ServerConnector> classType = resolveInheritedConnectorType(
                conf, tag);

        if (classType == null || classType == UnknownComponentConnector.class) {
            String serverSideName = conf.getUnknownServerClassNameByTag(tag);
            UnknownComponentConnector c = GWT
                    .create(UnknownComponentConnector.class);
            c.setServerSideClassName(serverSideName);
            return c;
        } else {
            /*
             * let the auto generated code instantiate this type
             */
            ServerConnector connector = widgetMap.instantiate(classType);
            if (connector instanceof HasJavaScriptConnectorHelper) {
                ((HasJavaScriptConnectorHelper) connector)
                        .getJavascriptConnectorHelper().setTag(tag);
            }
            return connector;
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
    public Class<? extends ServerConnector> getConnectorClassByTag(int tag,
            ApplicationConfiguration conf) {
        Class<? extends ServerConnector> connectorClass = null;
        Integer t = tag;
        do {
            String serverSideClassName = conf.getServerSideClassNameForTag(t);
            connectorClass = widgetMap
                    .getConnectorClassForServerSideClassName(serverSideClassName);
            t = conf.getParentTag(t);
        } while (connectorClass == UnknownComponentConnector.class && t != null);

        return connectorClass;
    }

    public Class<? extends ServerConnector>[] getDeferredLoadedConnectors() {
        return widgetMap.getDeferredLoadedConnectors();
    }

    public void loadImplementation(Class<? extends ServerConnector> nextType) {
        widgetMap.ensureInstantiator(nextType);
    }

}
