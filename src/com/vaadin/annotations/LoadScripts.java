/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Temporary hack used for ensuring external javascript libraries are included.
 * To add a javascript, add this annotation to your Root class.
 * 
 * @deprecated Will be removed in favor of a more robust solution before version
 *             7.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Deprecated
public @interface LoadScripts {
    public String[] value();

}
