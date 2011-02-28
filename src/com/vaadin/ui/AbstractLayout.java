/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Map;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
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

    private static final String CLICK_EVENT = EventId.LAYOUT_CLICK;

    protected MarginInfo margins = new MarginInfo(false);

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
     * @see com.vaadin.ui.Layout#setMargin(boolean, boolean, boolean, boolean)
     */
    public void setMargin(boolean topEnabled, boolean rightEnabled,
            boolean bottomEnabled, boolean leftEnabled) {
        margins.setMargins(topEnabled, rightEnabled, bottomEnabled, leftEnabled);
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractComponent#paintContent(com.vaadin
     * .terminal.PaintTarget)
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        // Add margin info. Defaults to false.
        target.addAttribute("margins", margins.getBitMask());

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractComponent#changeVariables(java.lang.Object,
     * java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
        // not all subclasses use these events
        if (this instanceof LayoutClickNotifier
                && variables.containsKey(CLICK_EVENT)) {
            fireClick((Map<String, Object>) variables.get(CLICK_EVENT));
        }

    }

    /**
     * Fire a layout click event.
     * 
     * Note that this method is only used by the subclasses that implement
     * {@link LayoutClickNotifier}, and can be overridden for custom click event
     * firing.
     * 
     * @param parameters
     *            The parameters received from the client side implementation
     */
    protected void fireClick(Map<String, Object> parameters) {
        MouseEventDetails mouseDetails = MouseEventDetails
                .deSerialize((String) parameters.get("mouseDetails"));
        Component clickedComponent = (Component) parameters.get("component");
        Component childComponent = clickedComponent;
        while (childComponent != null && childComponent.getParent() != this) {
            childComponent = childComponent.getParent();
        }

        fireEvent(new LayoutClickEvent(this, mouseDetails, clickedComponent,
                childComponent));
    }

}
