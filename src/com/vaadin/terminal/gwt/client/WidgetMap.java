/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.HashMap;

abstract class WidgetMap {

    protected static HashMap<Class, WidgetInstantiator> instmap = new HashMap<Class, WidgetInstantiator>();

    // FIXME: Should use Paintable and not VPaintableWidget
    public ComponentConnector instantiate(
            Class<? extends ComponentConnector> classType) {
        return instmap.get(classType).get();
    }

    // FIXME: Should use Paintable and not VPaintableWidget
    public abstract Class<? extends ComponentConnector> getImplementationByServerSideClassName(
            String fullyqualifiedName);

    // FIXME: Should use Paintable and not VPaintableWidget
    public abstract Class<? extends ComponentConnector>[] getDeferredLoadedWidgets();

    // FIXME: Should use Paintable and not VPaintableWidget
    public abstract void ensureInstantiator(
            Class<? extends ComponentConnector> classType);

}
