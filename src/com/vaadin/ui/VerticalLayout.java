/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import com.vaadin.terminal.gwt.client.ui.VVerticalLayout;

/**
 * Vertical layout
 * 
 * <code>VerticalLayout</code> is a component container, which shows the
 * subcomponents in the order of their addition (vertically). A vertical layout
 * is by default 100% wide.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.3
 */
@SuppressWarnings("serial")
@ClientWidget(value = VVerticalLayout.class, lazyLoad = false)
public class VerticalLayout extends AbstractOrderedLayout {

    public VerticalLayout() {
        setWidth("100%");
    }

}
