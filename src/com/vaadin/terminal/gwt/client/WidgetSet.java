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
     * Create an uninitialized component that best matches given UIDL. The
     * component must be a {@link Widget} that implements
     * {@link ComponentConnector}.
     * 
     * @param tag
     *            component type tag for the component to create
     * @param client
     *            the application connection that whishes to instantiate widget
     * 
     * @return New uninitialized and unregistered component that can paint given
     *         UIDL.
     */
    public ComponentConnector createWidget(int tag,
            ApplicationConfiguration conf) {
        /*
         * Yes, this (including the generated code in WidgetMap) may look very
         * odd code, but due the nature of GWT, we cannot do this any cleaner.
         * Luckily this is mostly written by WidgetSetGenerator, here are just
         * some hacks. Extra instantiation code is needed if client side widget
         * has no "native" counterpart on client side.
         */

        Class<? extends ComponentConnector> classType = resolveInheritedWidgetType(
                conf, tag);

        if (classType == null || classType == UnknownComponentConnector.class) {
            String serverSideName = conf
                    .getUnknownServerClassNameByTag(tag);
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

    private Class<? extends ComponentConnector> resolveInheritedWidgetType(
            ApplicationConfiguration conf, int tag) {
        Class<? extends ComponentConnector> classType = null;
        Integer t = tag;
        do {
            classType = resolveWidgetType(t, conf);
            t = conf.getParentTag(t);
        } while (classType == null && t != null);
        return classType;
    }

    protected Class<? extends ComponentConnector> resolveWidgetType(int tag,
            ApplicationConfiguration conf) {
        Class<? extends ComponentConnector> widgetClass = conf
                .getWidgetClassByEncodedTag(tag);

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
    public Class<? extends ComponentConnector> getConnectorClassByTag(int tag,
            ApplicationConfiguration conf) {
        Class<? extends ComponentConnector> connectorClass = null;
        Integer t = tag;
        do {
            connectorClass = widgetMap
                    .getConnectorClassForServerSideClassName(conf
                            .getServerSideClassNameForTag(t));
            t = conf.getParentTag(t);
        } while (connectorClass == UnknownComponentConnector.class && t != null);

        return connectorClass;
    }

    public Class<? extends ComponentConnector>[] getDeferredLoadedWidgets() {
        return widgetMap.getDeferredLoadedWidgets();
    }

    public void loadImplementation(Class<? extends ComponentConnector> nextType) {
        widgetMap.ensureInstantiator(nextType);
    }

}
