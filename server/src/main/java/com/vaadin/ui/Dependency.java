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
import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.HtmlImport;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.LegacyCommunicationManager;

/**
 * Represents a stylesheet or JavaScript to include on the page.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class Dependency implements Serializable {
    /**
     * The type of dependency.
     */
    public enum Type {
        STYLESHEET(StyleSheet.class), //
        JAVASCRIPT(JavaScript.class), //
        HTMLIMPORT(HtmlImport.class);

        private Class<? extends Annotation> annotationType;

        private Type(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }
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
     * Finds all the URLs defined for the given class using annotations for the
     * given type, registers the URLs to the communication manager and adds the
     * registered dependencies to the given list.
     *
     * @param type
     *            the type of dependencies to look for
     * @param cls
     *            the class to scan
     * @param manager
     *            a reference to the communication manager which tracks
     *            dependencies
     * @param dependencies
     *            the list to add registered dependencies to
     *
     * @return a stream of resource URLs in the order defined by the annotations
     */
    @SuppressWarnings("deprecation")
    private static void findAndRegisterResources(Type type,
            Class<? extends ClientConnector> cls,
            LegacyCommunicationManager manager, List<Dependency> dependencies) {
        Annotation[] annotations = cls
                .getAnnotationsByType(type.annotationType);
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                String[] resources;
                if (annotation instanceof StyleSheet) {
                    resources = ((StyleSheet) annotation).value();
                } else if (annotation instanceof JavaScript) {
                    resources = ((JavaScript) annotation).value();
                } else if (annotation instanceof HtmlImport) {
                    resources = ((HtmlImport) annotation).value();
                } else {
                    throw new IllegalArgumentException(
                            "Unknown annotation type: "
                                    + annotation.annotationType().getName());
                }

                for (String resource : resources) {
                    String url = manager.registerDependency(resource, cls);
                    dependencies.add(new Dependency(type, url));
                }
            }
        }
    }

    /**
     * Finds all the URLs defined for the given classes, registers the URLs to
     * the communication manager and returns the registered dependencies.
     * <p>
     * The returned collection contains all types of dependencies for each class
     * in the given list in the order the classes are in the list, i.e. all
     * dependencies for the first class before all dependencies for the next
     * class.
     * <p>
     * JavaScript dependencies are returned before HTML imports.
     *
     * @param connectorTypes
     *            the collection of connector classes to scan
     * @param manager
     *            a reference to the communication manager which tracks
     *            dependencies
     * @return
     */
    @SuppressWarnings("deprecation")
    public static List<Dependency> findDependencies(
            List<Class<? extends ClientConnector>> connectorTypes,
            LegacyCommunicationManager manager) {
        List<Dependency> dependencies = new ArrayList<>();

        for (Class<? extends ClientConnector> connectorType : connectorTypes) {
            findAndRegisterResources(Type.JAVASCRIPT, connectorType, manager,
                    dependencies);
            findAndRegisterResources(Type.HTMLIMPORT, connectorType, manager,
                    dependencies);
            findAndRegisterResources(Type.STYLESHEET, connectorType, manager,
                    dependencies);
        }

        return dependencies;
    }

}
