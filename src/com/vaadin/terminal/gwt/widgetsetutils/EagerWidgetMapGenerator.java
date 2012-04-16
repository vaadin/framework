/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.widgetsetutils;

import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ui.Connect.LoadStyle;

/**
 * WidgetMap generator that builds a widgetset that packs all included widgets
 * into a single JavaScript file loaded at application initialization. Initially
 * loaded data will be relatively large, but minimal amount of server requests
 * will be done.
 * <p>
 * This is the default generator in version 6.4 and produces similar type of
 * widgetset as in previous versions of Vaadin. To activate "code splitting",
 * use the {@link WidgetMapGenerator} instead, that loads most components
 * deferred.
 * 
 * @see WidgetMapGenerator
 * 
 */
public class EagerWidgetMapGenerator extends WidgetMapGenerator {

    @Override
    protected LoadStyle getLoadStyle(
            Class<? extends ComponentConnector> connector) {
        return LoadStyle.EAGER;
    }
}
