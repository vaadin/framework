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
 * framework ensures the referenced JavaScript files are loaded before the init
 * method for the corresponding client-side connector is invoked.
 * <p>
 * Absolute URLs including protocol and host are used as is on the client-side.
 * Relative urls are mapped to APP/CONNECTOR/[url] which are by default served
 * from the classpath relative to the class where the annotation is defined.
 * <p>
 * Example: {@code @JavaScript( "http://host.com/file1.js", "file2.js"})} on
 * the class com.example.MyConnector would load the file
 * http://host.com/file1.js as is and file2.js from /com/example/file2.js on the
 * server's classpath using the ClassLoader that was used to load
 * com.example.MyConnector.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JavaScript {
    /**
     * JavaScript files to load before initializing the client-side connector.
     * 
     * @return an array of JavaScript file urls
     */
    public String[] value();
}
