/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
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
     * connector must be a {@link Widget} that implements
     * {@link ServerConnector}.
     * 
     * @param tag
     *            connector type tag for the connector to create
     * @param client
     *            the application connection that whishes to instantiate widget
     * 
     * @return New uninitialized and unregistered connector that can paint given
     *         UIDL.
     */
    public ServerConnector createWidget(int tag, ApplicationConfiguration conf) {
        /*
         * Yes, this (including the generated code in WidgetMap) may look very
         * odd code, but due the nature of GWT, we cannot do this any cleaner.
         * Luckily this is mostly written by WidgetSetGenerator, here are just
         * some hacks. Extra instantiation code is needed if client side widget
         * has no "native" counterpart on client side.
         */

        Class<? extends ServerConnector> classType = resolveInheritedWidgetType(
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
            return widgetMap.instantiate(classType);
        }
    }

    private Class<? extends ServerConnector> resolveInheritedWidgetType(
            ApplicationConfiguration conf, int tag) {
        Class<? extends ServerConnector> classType = null;
        Integer t = tag;
        do {
            classType = resolveWidgetType(t, conf);
            t = conf.getParentTag(t);
        } while (classType == null && t != null);
        return classType;
    }

    protected Class<? extends ServerConnector> resolveWidgetType(int tag,
            ApplicationConfiguration conf) {
        Class<? extends ServerConnector> widgetClass = conf
                .getConnectorClassByEncodedTag(tag);

        return widgetClass;
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
