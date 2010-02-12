package com.vaadin.terminal.gwt.client.ui.dd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerCriterion {
    /**
     * Class type would be nice but annotating should come from different
     * direction to cope with gwt compiler.
     * 
     * @return
     */
    String value();
}
