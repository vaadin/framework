/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.ui;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import com.vaadin.annotations.HtmlImport;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.ClientConnector;

/**
 * Represents a stylesheet or JavaScript to include on the page.
 *
 * @author Vaadin Ltd
 */
public class Dependency implements Serializable {
    /**
     * The type of dependency.
     */
    public enum Type {
        STYLESHEET, JAVASCRIPT, HTMLIMPORT;
    }

    private final Type type;
    private final String url;

    /**
     * Creates a new dependency of the given type, to be loaded from the given
     * URL.
     * <p>
     * The URL is passed through the translation mechanism before loading, so
     * custom protocols such as "vaadin://" can be used.
     *
     * @param type
     *            the type of dependency, not <code>null</code>
     * @param url
     *            the URL to load the dependency from, not <code>null</code>
     */
    public Dependency(Type type, String url) {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null");
        }
        assert type != null;
        this.type = type;
        this.url = url;
    }

    /**
     * Gets the untranslated URL for the dependency.
     *
     * @return the URL for the dependency
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the type of the dependency.
     *
     * @return the type of the dependency
     */
    public Type getType() {
        return type;
    }

    /**
     * Finds all the URLs defined for the given class using {@code @JavaScript},
     * {@code @HtmlImport} or {@code @StyleSheet}.
     *
     * @param annotationType
     *            the annotation type, must be one of {@code @JavaScript},
     *            {@code @HtmlImport} or {@code @StyleSheet}
     * @param cls
     *            the class to scan
     * @return a stream of resource URLs in the order defined by the annotations
     */
    public static Stream<String> findAnnotatedResources(
            Class<? extends Annotation> annotationType,
            Class<? extends ClientConnector> cls) {
        Stream<String> stream = Stream.empty();
        Annotation[] annotations = cls.getAnnotationsByType(annotationType);
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                String[] resources;
                if (annotation.annotationType() == StyleSheet.class) {
                    resources = ((StyleSheet) annotation).value();
                } else if (annotation.annotationType() == JavaScript.class) {
                    resources = ((JavaScript) annotation).value();
                } else if (annotation.annotationType() == HtmlImport.class) {
                    resources = ((HtmlImport) annotation).value();
                } else {
                    throw new IllegalArgumentException(
                            "Unknown annotation type: "
                                    + annotation.annotationType().getName());
                }
                stream = Stream.concat(stream, Stream.of(resources));
            }
        }
        return stream;
    }

}
