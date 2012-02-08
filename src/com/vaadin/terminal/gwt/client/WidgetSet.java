/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.VUnknownComponentPaintable;

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
     * {@link VPaintableWidget}.
     * 
     * @param tag
     *            component type tag for the component to create
     * @param client
     *            the application connection that whishes to instantiate widget
     * 
     * @return New uninitialized and unregistered component that can paint given
     *         UIDL.
     */
    public VPaintableWidget createWidget(String tag,
            ApplicationConfiguration conf) {
        /*
         * Yes, this (including the generated code in WidgetMap) may look very
         * odd code, but due the nature of GWT, we cannot do this any cleaner.
         * Luckily this is mostly written by WidgetSetGenerator, here are just
         * some hacks. Extra instantiation code is needed if client side widget
         * has no "native" counterpart on client side.
         */

        Class<? extends VPaintableWidget> classType = resolveWidgetType(tag,
                conf);

        if (classType == null || classType == VUnknownComponentPaintable.class) {
            String serverSideName = conf
                    .getUnknownServerClassNameByEncodedTagName(tag);
            VUnknownComponentPaintable c = GWT
                    .create(VUnknownComponentPaintable.class);
            c.setServerSideClassName(serverSideName);
            return c;
        } else {
            /*
             * let the auto generated code instantiate this type
             */
            return widgetMap.instantiate(classType);
        }

    }

    protected Class<? extends VPaintableWidget> resolveWidgetType(String tag,
            ApplicationConfiguration conf) {
        Class<? extends VPaintableWidget> widgetClass = conf
                .getWidgetClassByEncodedTag(tag);

        return widgetClass;

    }

    /**
     * Due its nature, GWT does not support dynamic classloading. To bypass this
     * limitation, widgetset must have function that returns Class by its fully
     * qualified name.
     * 
     * @param fullyQualifiedName
     * @param applicationConfiguration
     * @return
     */
    public Class<? extends VPaintableWidget> getImplementationByClassName(
            String fullyqualifiedName) {
        if (fullyqualifiedName == null) {
            return VUnknownComponentPaintable.class;
        }
        Class<? extends VPaintableWidget> implementationByServerSideClassName = widgetMap
                .getImplementationByServerSideClassName(fullyqualifiedName);

        return implementationByServerSideClassName;

    }

    public Class<? extends VPaintableWidget>[] getDeferredLoadedWidgets() {
        return widgetMap.getDeferredLoadedWidgets();
    }

    public void loadImplementation(Class<? extends VPaintableWidget> nextType) {
        widgetMap.ensureInstantiator(nextType);
    }

}
