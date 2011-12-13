/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.ui.Root;

/**
 * Defines a specific theme for a {@link Root}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RootTheme {
    /**
     * @return simple name of the theme
     */
    public String value();
}
