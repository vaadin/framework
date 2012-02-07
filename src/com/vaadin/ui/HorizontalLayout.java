/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import com.vaadin.terminal.gwt.client.ui.VHorizontalLayoutPaintable;
import com.vaadin.ui.ClientWidget.LoadStyle;

/**
 * Horizontal layout
 * 
 * <code>HorizontalLayout</code> is a component container, which shows the
 * subcomponents in the order of their addition (horizontally).
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 5.3
 */
@SuppressWarnings("serial")
@ClientWidget(value = VHorizontalLayoutPaintable.class, loadStyle = LoadStyle.EAGER)
public class HorizontalLayout extends AbstractOrderedLayout {

    public HorizontalLayout() {

    }

}
