/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

/**
 * Indicates that the init method in a Root class should not be called until
 * full browser details ({@link WrappedRequest#getBrowserDetails()}) are
 * available. Ensuring the availability of this information will typically
 * requires an additional round trip to the client, which will cause the
 * application startup to progress more slowly.
 * 
 * @see Root#init(com.vaadin.terminal.WrappedRequest)
 * @see WrappedRequest#getBrowserDetails()
 * 
 * @since 7.0
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RootInitRequiresBrowserDetails {
    // No methods
}
