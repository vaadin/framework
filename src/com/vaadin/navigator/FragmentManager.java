/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.navigator;

import java.io.Serializable;

/**
 * Fragment manager that handles interaction between Navigator and URI fragments
 * or other similar view identification and bookmarking system.
 * 
 * Alternative implementations can be created for HTML5 pushState, for portlet
 * URL navigation and other similar systems.
 * 
 * This interface is mostly for internal use by {@link Navigator}.
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public interface FragmentManager extends Serializable {
    /**
     * Return the current fragment (location string) including view name and any
     * optional parameters.
     * 
     * @return current view and parameter string, not null
     */
    public String getFragment();

    /**
     * Set the current fragment (location string) in the application URL or
     * similar location, including view name and any optional parameters.
     * 
     * @param fragment
     *            new view and parameter string, not null
     */
    public void setFragment(String fragment);
}