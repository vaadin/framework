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

package com.vaadin.server;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.ui.UI;

public abstract class UIProvider implements Serializable {
    public abstract Class<? extends UI> getUIClass(UIClassSelectionEvent event);

    public UI createInstance(UICreateEvent event) {
        try {
            return event.getUIClass().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not instantiate root class", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not access root class", e);
        }
    }

    /**
     * Helper to get an annotation for a class. If the annotation is not present
     * on the target class, it's super classes and implemented interfaces are
     * also searched for the annotation.
     * 
     * @param event
     *            the UI create event to get required info from
     * @param annotationType
     *            the annotation type to look for
     * @return an annotation of the given type, or <code>null</code> if the
     *         annotation is not present on the class
     */
    protected static <T extends Annotation> T getAnnotationFor(
            UICreateEvent event, Class<T> annotationType) {
        // Find from the class hierarchy
        Class<?> currentType = event.getUIClass();
        while (currentType != Object.class) {
            T annotation = currentType.getAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            } else {
                currentType = currentType.getSuperclass();
            }
        }

        // Find from an implemented interface
        for (Class<?> iface : event.getUIClass().getInterfaces()) {
            T annotation = iface.getAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    /**
     * Finds the theme to use for a specific UI. If no specific theme is
     * required, <code>null</code> is returned.
     * <p>
     * The default implementation checks for a @{@link Theme} annotation on the
     * UI class.
     * 
     * @param event
     *            the UI create event with information about the UI and the
     *            current request.
     * @return the name of the theme, or <code>null</code> if the default theme
     *         should be used
     * 
     */
    public String getTheme(UICreateEvent event) {
        Theme uiTheme = getAnnotationFor(event, Theme.class);
        if (uiTheme != null) {
            return uiTheme.value();
        } else {
            return null;
        }
    }

    /**
     * Finds the widgetset to use for a specific UI. If no specific widgetset is
     * required, <code>null</code> is returned.
     * <p>
     * The default implementation uses the @{@link Widgetset} annotation if it's
     * defined for the UI class.
     * 
     * @param event
     *            the UI create event with information about the UI and the
     *            current request.
     * @return the name of the widgetset, or <code>null</code> if the default
     *         widgetset should be used
     * 
     */
    public String getWidgetset(UICreateEvent event) {
        Widgetset uiWidgetset = getAnnotationFor(event, Widgetset.class);
        if (uiWidgetset != null) {
            return uiWidgetset.value();
        } else {
            return null;
        }
    }

    /**
     * Checks whether the same UI state should be reused if the framework can
     * detect that the application is opened in a browser window where it has
     * previously been open. The framework attempts to discover this by checking
     * the value of window.name in the browser.
     * 
     * @param event
     *            the UI create event with information about the UI and the
     *            current request.
     * 
     * @return <code>true</code>if the same UI instance should be reused e.g.
     *         when the browser window is refreshed.
     */
    public boolean isPreservedOnRefresh(UICreateEvent event) {
        PreserveOnRefresh preserveOnRefresh = getAnnotationFor(event,
                PreserveOnRefresh.class);
        return preserveOnRefresh != null;
    }

    public String getPageTitle(UICreateEvent event) {
        Title titleAnnotation = getAnnotationFor(event, Title.class);
        if (titleAnnotation == null) {
            return null;
        } else {
            return titleAnnotation.value();
        }
    }
}
