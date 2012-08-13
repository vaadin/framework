/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.shared.ui.dd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation type used to point the server side counterpart for client side
 * a {@link com.vaadin.terminal.gwt.client.ui.dd.VAcceptCriterion} class.
 * <p>
 * Annotations are used at GWT compilation phase, so remember to rebuild your
 * widgetset if you do changes for {@link AcceptCriterion} mappings.
 * 
 * Prior to Vaadin 7, the mapping was done with an annotation on server side
 * classes.
 * 
 * @since 7.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AcceptCriterion {
    /**
     * @return the class of the server side counterpart for the annotated
     *         criterion
     */
    Class<?> value();

}
