package com.vaadin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

/**
 * Indicates that the init method in a Root class can be called before full
 * browser details ({@link WrappedRequest#getBrowserDetails()}) are available.
 * This will make the UI appear more quickly, as ensuring the availability of
 * this information typically requires an additional round trip to the client.
 * 
 * @see Root#init(com.vaadin.terminal.WrappedRequest)
 * @see WrappedRequest#getBrowserDetails()
 * 
 * @since 7.0
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EagerInit {
    // No values
}
