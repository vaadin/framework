/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.navigator;

import java.io.Serializable;

/**
 * Interface for listening to View changes before and after they occur.
 * 
 * Implementations of this interface can also block navigation between views
 * before it is performed.
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public interface ViewChangeListener extends Serializable {

    /**
     * Check whether changing the view is permissible.
     * 
     * This method may also e.g. open a "save" dialog or question about the
     * change, which may re-initiate the navigation operation after user action.
     * 
     * If this listener does not want to block the view change (e.g. does not
     * know the view in question), it should return true. If any listener
     * returns false, the view change is not allowed.
     * 
     * TODO move to separate interface?
     * 
     * @param previous
     *            view that is being deactivated
     * @param next
     *            view that is being activated
     * @param viewName
     *            name of the new view that is being activated
     * @param fragmentParameters
     *            fragment parameters (potentially bookmarkable) for the new
     *            view
     * @param internalParameters
     *            internal parameters for the new view, not visible in the
     *            browser
     * @return true if the view change should be allowed or this listener does
     *         not care about the view change, false to block the change
     */
    public boolean isViewChangeAllowed(View previous, View next,
            String viewName, String fragmentParameters,
            Object... internalParameters);

    /**
     * Invoked after the view has changed. Be careful for deadlocks if you
     * decide to change the view again in the listener.
     * 
     * @param previous
     *            Preview view before the change.
     * @param current
     *            New view after the change.
     */
    public void navigatorViewChanged(View previous, View current);

}