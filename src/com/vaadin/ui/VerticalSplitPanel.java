/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import com.vaadin.terminal.gwt.client.ui.VVerticalSplitPanelPaintable;
import com.vaadin.ui.ClientWidget.LoadStyle;

/**
 * A vertical split panel contains two components and lays them vertically. The
 * first component is above the second component.
 * 
 * <pre>
 *      +--------------------------+
 *      |                          |
 *      |  The first component     |
 *      |                          |
 *      +==========================+  <-- splitter
 *      |                          |
 *      |  The second component    |
 *      |                          |
 *      +--------------------------+
 * </pre>
 * 
 */
@ClientWidget(value = VVerticalSplitPanelPaintable.class, loadStyle = LoadStyle.EAGER)
public class VerticalSplitPanel extends AbstractSplitPanel {

    public VerticalSplitPanel() {
        super();
        setSizeFull();
    }

}
