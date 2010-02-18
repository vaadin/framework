package com.vaadin.event.dd.acceptCriteria;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.terminal.gwt.client.ui.dd.VAcceptCriterion;

/**
 * TODO
 * 
 * @since 6.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClientCriterion {
    /**
     * @return the client side counterpart for the annotated criterion
     */
    Class<? extends VAcceptCriterion> value();
}
