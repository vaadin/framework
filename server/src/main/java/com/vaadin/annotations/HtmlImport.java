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
package com.vaadin.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.server.ClientConnector;

/**
 * If this annotation is present on a {@link ClientConnector} class, the
 * framework ensures the referenced HTML imports are loaded before the init
 * method for the corresponding client-side connector is invoked.
 * <p>
 * Note that not all browsers yet support HTML imports. If a polyfill is needed
 * to load HTML imports, it must be loaded before HTML Imports can be loaded.
 * There is no automatic loading of any polyfill.
 * <p>
 * <ul>
 * <li>Relative URLs are mapped to APP/PUBLISHED/[url] which are by default
 * served from the classpath relative to the class where the annotation is
 * defined.
 * <li>Absolute URLs including protocol and host are used as is on the
 * client-side.
 * </ul>
 * Note that it is a good idea to use URLs starting with {@literal vaadin://}
 * and place all HTML imports inside {@literal VAADIN/bower_components}. Polymer
 * elements rely on importing dependencies using relative paths
 * {@literal ../../other-element/other-element.html}, which will not work if
 * they are installed in different locations.
 * <p>
 * HTML imports are added to the page after any {@code @JavaScript} dependencies
 * added at the same time.
 * <p>
 * Example:
 * <code>@HtmlImport("bower_components/paper-slider/paper-slider.html")</code>
 * on the class com.example.MyConnector would load the file
 * http://host.com/VAADIN/bower_components/paper-slider/paper-slider.html before
 * the {@code init()} method of the client side connector is invoked.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(InternalContainerAnnotationForHtml.class)
public @interface HtmlImport {

    /**
     * HTML file URL(s) to load before using the annotated
     * {@link ClientConnector} in the browser.
     *
     * @return html file URL(s) to load
     */
    String[] value();
}
