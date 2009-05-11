/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

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
     */
    public void iLayout();
}
