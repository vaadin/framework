/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.event.dd.acceptcriteria;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.terminal.gwt.client.ui.dd.VAcceptCriterion;

/**
 * An annotation type used to point the client side counterpart for server side
 * a {@link AcceptCriterion} class.
 * <p>
 * Annotations are used at GWT compilation phase, so remember to rebuild your
 * widgetset if you do changes for {@link ClientCriterion} mappings.
 * 
 * @since 6.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClientCriterion {
    /**
     * @return the client side counterpart for the annotated criterion
     */
    Class<? extends VAcceptCriterion> value();
}
