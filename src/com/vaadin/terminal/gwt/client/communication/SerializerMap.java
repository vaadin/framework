/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import com.vaadin.terminal.gwt.widgetsetutils.SerializerMapGenerator;

/**
 * Provide a mapping from a type (communicated between the server and the
 * client) and a {@link VaadinSerializer} instance.
 * 
 * An implementation of this class is created at GWT compilation time by
 * {@link SerializerMapGenerator}, so this interface can be instantiated with
 * GWT.create().
 * 
 * @since 7.0
 */
public interface SerializerMap {

    /**
     * Returns a serializer instance for a given type.
     * 
     * @param type
     *            type communicated on between the server and the client
     *            (currently fully qualified class name)
     * @return serializer instance, not null
     * @throws RuntimeException
     *             if no serializer is found
     */
    // TODO better error handling in javadoc and in generator
    public VaadinSerializer getSerializer(String type);

}
