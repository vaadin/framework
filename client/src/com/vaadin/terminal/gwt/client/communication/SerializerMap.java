/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.terminal.gwt.client.communication;

/**
 * Provide a mapping from a type (communicated between the server and the
 * client) and a {@link JSONSerializer} instance.
 * 
 * An implementation of this class is created at GWT compilation time by
 * SerializerMapGenerator, so this interface can be instantiated with
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
    public JSONSerializer getSerializer(String type);

}
