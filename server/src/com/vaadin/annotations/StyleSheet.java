/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.terminal.gwt.server.ClientConnector;

/**
 * If this annotation is present on a {@link ClientConnector} class, the
 * framework ensures the referenced style sheets are loaded before the init
 * method for the corresponding client-side connector is invoked.
 * <p>
 * Example: {@code @StyleSheet( "http://host.com/file1.css", "file2.css"})} on
 * the class com.example.MyConnector would load the file
 * http://host.com/file1.css as is and file2.css from /com/example/file2.css on
 * the server's classpath using the ClassLoader that was used to load
 * com.example.MyConnector.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StyleSheet {
    /**
     * Style sheets to load before initializing the client-side connector.
     * 
     * @return an array of style sheet urls
     */
    public String[] value();
}
