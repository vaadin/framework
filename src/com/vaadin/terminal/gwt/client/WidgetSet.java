/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Widget;

public interface WidgetSet extends EntryPoint {

    /**
     * Create an uninitialized component that best matches given UIDL. The
     * component must be a {@link Widget} that implements {@link Paintable}.
     * 
     * @param uidl
     *            UIDL to be painted with returned component.
     * @return New uninitialized and unregistered component that can paint given
     *         UIDL.
     */
    public Paintable createWidget(UIDL uidl);

    /**
     * Test if the given component implementation conforms to UIDL.
     * 
     * @param currentWidget
     *            Current implementation of the component
     * @param uidl
     *            UIDL to test against
     * @return true iff createWidget would return a new component of the same
     *         class than currentWidget
     */
    public boolean isCorrectImplementation(Widget currentWidget, UIDL uidl);
}
