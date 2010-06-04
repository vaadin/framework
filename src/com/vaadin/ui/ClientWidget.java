/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.widgetsetutils.WidgetMapGenerator;

/**
 * Annotation defining the default client side counterpart in GWT terminal for
 * {@link Component}.
 * <p>
 * With this annotation server side Vaadin component is marked to have a client
 * side counterpart. The value of the annotation is the class of client side
 * implementation.
 * 
 * <p>
 * Note, even though client side implementation is needed during development,
 * one may safely remove them from classpath of the production server.
 * 
 * 
 * @since 6.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClientWidget {
    /**
     * @return the client side counterpart for the annotated component
     */
    Class<? extends Paintable> value();

    /**
     * The lazy loading of a widget implementation means the client side
     * component is not included in the initial JavaScript application loaded
     * when the application starts. Instead the implementation is loaded to the
     * client when it is first needed.
     * <p>
     * Lazy loaded widgets don't stress the size and startup time of the client
     * side as much as eagerly loaded widgets. On the other hand there is a
     * slight latency when lazy loaded widgets are first used as the client side
     * needs to visit the server to fetch the client side implementation.
     * <p>
     * In common situations the default value should be fine. If the widget
     * implementation commonly used and often on first view it is better set
     * lazy loading off for it. Also if the component implementation is really
     * thing, it may by justified to make the widget implementation eagerly
     * loaded.
     * <p>
     * Tunings to widget loading can also be made by overriding
     * {@link WidgetMapGenerator} in GWT module. Tunings might be helpful if the
     * end users have slow connections and especially if they have high latency
     * in their network.
     * 
     * @return if true (default), the GWT code generator will make the client
     *         side implementation lazy loaded. Displaying it first time on the
     *         screen slightly increases, but widgets implementation does not
     *         stress the initialization of the client side engine.
     */
    boolean lazyLoad() default true;

}
