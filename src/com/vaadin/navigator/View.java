/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.navigator;

import java.io.Serializable;

import com.vaadin.ui.Component;

/**
 * Interface for all views controlled by the navigator.
 * 
 * Each view added to the navigator must implement this interface. Typically, a
 * view is a {@link Component}.
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public interface View extends Serializable {

    /**
     * Init view.
     * 
     * Convenience method which navigator calls just before the view is rendered
     * for the first time. This is called only once in the lifetime of each view
     * instance.
     */
    public void init();

    /**
     * This view is navigated to.
     * 
     * This method is always called before the view is shown on screen. If there
     * is any additional id to data what should be shown in the view, it is also
     * optionally passed as parameter.
     * 
     * TODO fragmentParameters null if no parameters or empty string?
     * 
     * @param fragmentParameters
     *            parameters to the view or null if none given. This is the
     *            string that appears e.g. in URI after "viewname/"
     * @param internalParameters
     *            parameters given directly to
     *            {@link Navigator#navigateTo(String, Object...)}, not passed
     *            via the fragment and not preserved in bookmarks
     */
    public void navigateTo(String fragmentParameters,
            Object... internalParameters);
}