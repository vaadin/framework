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
     */
    public void navigateTo(String fragmentParameters);
}