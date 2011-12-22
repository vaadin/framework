/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.fieldgroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyId {
    String value();
}
