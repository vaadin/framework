/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

/**
 * ContainerResizedListener interface is useful for Widgets that support
 * relative sizes and who need some additional sizing logic.
 */
public interface ContainerResizedListener {
    /**
     * This function is run when container box has been resized. Object
     * implementing ContainerResizedListener is responsible to call the same
     * function on its ancestors that implement NeedsLayout in case their
     * container has resized. runAnchestorsLayout(HasWidgets parent) function
     * from Util class may be a good helper for this.
     * 
     * The width and height parameters specifies the space available for the
     * component (in pixels) if the parent container can or want to produce
     * these numbers. If the parent container does not know (has not calculated)
     * or cannot produce (undefined dimensions) one of these numbers -1 is
     * passed.
     */
    public void iLayout(int availableWidth, int availableHeight);
}
