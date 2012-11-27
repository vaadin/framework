package com.vaadin.client.ui.colorpicker;

import java.util.Set;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.colorpicker.ColorPickerState;

/**
 * An abstract class that defines default implementation for a color picker
 * connector.
 * 
 * @since 7.0.0
 */
public abstract class AbstractColorPickerConnector extends
        AbstractComponentConnector implements ClickHandler {

    @Override
    public ColorPickerState getState() {
        return (ColorPickerState) super.getState();
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        // NOTE: this method is called after @DelegateToWidget
        super.onStateChanged(stateChangeEvent);
        Set<String> changedProperties = stateChangeEvent.getChangedProperties();
        if (changedProperties.contains("color")) {
            refreshColor();

            if (getState().showDefaultCaption
                    && (getState().caption == null || ""
                            .equals(getState().caption))) {

                setCaption(getState().color);
            }
        }
        if (changedProperties.contains("caption")
                || changedProperties.contains("htmlContentAllowed")
                || changedProperties.contains("showDefaultCaption")) {

            setCaption(getCaption());
        }
    }

    @Override
    public void init() {
        super.init();
        if (getWidget() instanceof HasClickHandlers) {
            ((HasClickHandlers) getWidget()).addClickHandler(this);
        }
    }

    /**
     * Get caption for the color picker widget.
     * 
     * @return
     */
    protected String getCaption() {
        if (getState().showDefaultCaption
                && (getState().caption == null || "".equals(getState().caption))) {
            return getState().color;
        }
        return getState().caption;
    }

    /**
     * Set caption of the color picker widget.
     * 
     * @param caption
     */
    protected abstract void setCaption(String caption);

    /**
     * Update the widget to show the currently selected color.
     */
    protected abstract void refreshColor();

}
