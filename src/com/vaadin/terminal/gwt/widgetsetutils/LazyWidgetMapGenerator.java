/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.widgetsetutils;

import com.vaadin.terminal.Paintable;
import com.vaadin.ui.ClientWidget.LoadStyle;

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
    protected LoadStyle getLoadStyle(Class<? extends Paintable> paintableType) {
        return LoadStyle.LAZY;
    }

}
