/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.HashMap;

abstract class WidgetMap {

    protected static HashMap<Class, WidgetInstantiator> instmap = new HashMap<Class, WidgetInstantiator>();

    // FIXME: Should use Paintable and not VPaintableWidget
    public VPaintableWidget instantiate(
            Class<? extends VPaintableWidget> classType) {
        return instmap.get(classType).get();
    }

    // FIXME: Should use Paintable and not VPaintableWidget
    public abstract Class<? extends VPaintableWidget> getImplementationByServerSideClassName(
            String fullyqualifiedName);

    // FIXME: Should use Paintable and not VPaintableWidget
    public abstract Class<? extends VPaintableWidget>[] getDeferredLoadedWidgets();

    // FIXME: Should use Paintable and not VPaintableWidget
    public abstract void ensureInstantiator(
            Class<? extends VPaintableWidget> classType);

}
