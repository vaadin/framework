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
 * {@link Component}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClientWidget {
    Class<? extends Paintable> value();
}
