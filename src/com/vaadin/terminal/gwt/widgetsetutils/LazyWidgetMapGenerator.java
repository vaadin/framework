/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.widgetsetutils;

import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.terminal.gwt.client.ServerConnector;

/**
 * WidgetMap generator that builds a widgetset that optimizes the transferred
 * data. Widgets are loaded only when used if the widgetset is built with this
 * generator.
 * 
 * @see WidgetMapGenerator
 * 
 */
public class LazyWidgetMapGenerator extends WidgetMapGenerator {
    @Override
    protected LoadStyle getLoadStyle(Class<? extends ServerConnector> connector) {
        return LoadStyle.LAZY;
    }

}
