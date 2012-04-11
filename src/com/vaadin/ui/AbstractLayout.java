/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.terminal.gwt.client.ui.AbstractLayoutState;
import com.vaadin.ui.Layout.MarginHandler;

/**
 * An abstract class that defines default implementation for the {@link Layout}
 * interface.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
@SuppressWarnings("serial")
public abstract class AbstractLayout extends AbstractComponentContainer
        implements Layout, MarginHandler {

    protected MarginInfo margins = new MarginInfo(false);

    @Override
    public AbstractLayoutState getState() {
        return (AbstractLayoutState) super.getState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout#setMargin(boolean)
     */
    public void setMargin(boolean enabled) {
        margins.setMargins(enabled);
        getState().setMarginsBitmask(margins.getBitMask());
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.MarginHandler#getMargin()
     */
    public MarginInfo getMargin() {
        return margins;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.MarginHandler#setMargin(MarginInfo)
     */
    public void setMargin(MarginInfo marginInfo) {
        margins.setMargins(marginInfo);
        getState().setMarginsBitmask(margins.getBitMask());
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout#setMargin(boolean, boolean, boolean, boolean)
     */
    public void setMargin(boolean topEnabled, boolean rightEnabled,
            boolean bottomEnabled, boolean leftEnabled) {
        margins.setMargins(topEnabled, rightEnabled, bottomEnabled, leftEnabled);
        getState().setMarginsBitmask(margins.getBitMask());
        requestRepaint();
    }

}
