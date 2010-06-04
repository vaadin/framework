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
     * @param client
     *            the application connection that whishes to instantiate widget
     * 
     * @return New uninitialized and unregistered component that can paint given
     *         UIDL.
     */
    public Paintable createWidget(UIDL uidl, ApplicationConfiguration conf);

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
    public boolean isCorrectImplementation(Widget currentWidget, UIDL uidl,
            ApplicationConfiguration conf);

    /**
     * Due its nature, GWT does not support dynamic classloading. To bypass this
     * limitation, widgetset must have function that returns Class by its fully
     * qualified name.
     * 
     * @param fullyQualifiedName
     * @param applicationConfiguration
     * @return
     */
    public Class<? extends Paintable> getImplementationByClassName(
            String fullyQualifiedName,
            ApplicationConfiguration applicationConfiguration);

}
