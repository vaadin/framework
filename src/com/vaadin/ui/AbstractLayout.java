/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.Layout.MarginHandler;

/**
 * An abstract class that defines default implementation for the {@link Layout}
 * interface.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
@SuppressWarnings("serial")
public abstract class AbstractLayout extends AbstractComponentContainer
        implements Layout, MarginHandler {

    protected MarginInfo margins = new MarginInfo(false);

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractComponent#getTag()
     */
    @Override
    public abstract String getTag();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout#setMargin(boolean)
     */
    public void setMargin(boolean enabled) {
        margins.setMargins(enabled);
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
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout#setMargin(boolean, boolean, boolean,
     * boolean)
     */
    public void setMargin(boolean topEnabled, boolean rightEnabled,
            boolean bottomEnabled, boolean leftEnabled) {
        margins
                .setMargins(topEnabled, rightEnabled, bottomEnabled,
                        leftEnabled);
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.AbstractComponent#paintContent(com.vaadin
     * .terminal.PaintTarget)
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        // Add margin info. Defaults to false.
        target.addAttribute("margins", margins.getBitMask());

    }

}
