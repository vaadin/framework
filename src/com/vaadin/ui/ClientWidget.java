/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.terminal.gwt.client.Paintable;

/**
 * Annotation defining the default client side counterpart in GWT terminal for
 * {@link Component}.
 * <p>
 * With this annotation server side Vaadin component is marked to have a client
 * side counterpart. The value of the annotation is the class of client side
 * implementation.
 * 
 * <p>
 * Note, even though client side implementation is needed during development,
 * one may safely remove them from classpath of the production server.
 * 
 * 
 * @since 6.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClientWidget {
    /**
     * @return the client side counterpart for the annotated component
     */
    Class<? extends Paintable> value();
}
