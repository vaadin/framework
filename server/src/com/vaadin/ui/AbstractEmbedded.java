/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.AbstractEmbeddedState;
import com.vaadin.terminal.gwt.server.ResourceReference;

/**
 * Abstract base for embedding components.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 7.0
 */
@SuppressWarnings("serial")
public abstract class AbstractEmbedded extends AbstractComponent {

    @Override
    public AbstractEmbeddedState getState() {
        return (AbstractEmbeddedState) super.getState();
    }

    /**
     * Sets the object source resource. The dimensions are assumed if possible.
     * The type is guessed from resource.
     * 
     * @param source
     *            the source to set.
     */
    public void setSource(Resource source) {
        if (source == null) {
            getState().setSource(null);
        } else {
            getState().setSource(new ResourceReference(source));
        }
        requestRepaint();
    }

    /**
     * Get the object source resource.
     * 
     * @return the source
     */
    public Resource getSource() {
        ResourceReference ref = ((ResourceReference) getState().getSource());
        if (ref == null) {
            return null;
        } else {
            return ref.getResource();
        }
    }

    /**
     * Sets this component's alternate text that can be presented instead of the
     * component's normal content for accessibility purposes.
     * 
     * @param altText
     *            A short, human-readable description of this component's
     *            content.
     */
    public void setAlternateText(String altText) {
        if (altText != getState().getAlternateText()
                || (altText != null && !altText.equals(getState()
                        .getAlternateText()))) {
            getState().setAlternateText(altText);
            requestRepaint();
        }
    }

    /**
     * Gets this component's alternate text that can be presented instead of the
     * component's normal content for accessibility purposes.
     * 
     * @returns Alternate text
     */
    public String getAlternateText() {
        return getState().getAlternateText();
    }

}
