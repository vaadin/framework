/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.shared.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface DelegateToWidget {
    public String value() default "";

    public static class Helper {
        public static String getDelegateTarget(String propertyName,
                String annotationValue) {
            String name = annotationValue;
            if (name.isEmpty()) {
                name = "set" + Character.toUpperCase(propertyName.charAt(0))
                        + propertyName.substring(1);
            }
            return name;
        }
    }
}
