/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.AbstractEmbeddedState;

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
    protected AbstractEmbeddedState getState() {
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
        setResource(AbstractEmbeddedState.SOURCE_RESOURCE, source);
    }

    /**
     * Get the object source resource.
     * 
     * @return the source
     */
    public Resource getSource() {
        return getResource(AbstractEmbeddedState.SOURCE_RESOURCE);
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
        if (altText != getState().alternateText
                || (altText != null && !altText
                        .equals(getState().alternateText))) {
            getState().alternateText = altText;
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
        return getState().alternateText;
    }

}
