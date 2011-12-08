/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.HashMap;

abstract class WidgetMap {

    protected static HashMap<Class, WidgetInstantiator> instmap = new HashMap<Class, WidgetInstantiator>();

    public Paintable instantiate(Class<? extends Paintable> classType) {
        return instmap.get(classType).get();
    }

    public abstract Class<? extends Paintable> getImplementationByServerSideClassName(
            String fullyqualifiedName);

    public abstract Class<? extends Paintable>[] getDeferredLoadedWidgets();

    public abstract void ensureInstantiator(Class<? extends Paintable> classType);

}
