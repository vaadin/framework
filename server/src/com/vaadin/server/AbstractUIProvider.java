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

import java.lang.annotation.Annotation;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.ui.UI;

public abstract class AbstractUIProvider implements UIProvider {

    @Override
    public UI createInstance(WrappedRequest request,
            Class<? extends UI> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not instantiate root class", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not access root class", e);
        }
    }

    /**
     * Helper to get an annotation for a class. If the annotation is not present
     * on the target class, it's superclasses and implemented interfaces are
     * also searched for the annotation.
     * 
     * @param type
     *            the target class from which the annotation should be found
     * @param annotationType
     *            the annotation type to look for
     * @return an annotation of the given type, or <code>null</code> if the
     *         annotation is not present on the class
     */
    protected static <T extends Annotation> T getAnnotationFor(Class<?> type,
            Class<T> annotationType) {
        // Find from the class hierarchy
        Class<?> currentType = type;
        while (currentType != Object.class) {
            T annotation = currentType.getAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            } else {
                currentType = currentType.getSuperclass();
            }
        }

        // Find from an implemented interface
        for (Class<?> iface : type.getInterfaces()) {
            T annotation = iface.getAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    @Override
    public String getTheme(WrappedRequest request,
            Class<? extends UI> uiClass) {
        Theme uiTheme = getAnnotationFor(uiClass, Theme.class);
        if (uiTheme != null) {
            return uiTheme.value();
        } else {
            return null;
        }
    }

    @Override
    public String getWidgetset(WrappedRequest request,
            Class<? extends UI> uiClass) {
        Widgetset uiWidgetset = getAnnotationFor(uiClass, Widgetset.class);
        if (uiWidgetset != null) {
            return uiWidgetset.value();
        } else {
            return null;
        }
    }

    @Override
    public boolean isPreservedOnRefresh(WrappedRequest request,
            Class<? extends UI> uiClass) {
        PreserveOnRefresh preserveOnRefresh = getAnnotationFor(uiClass,
                PreserveOnRefresh.class);
        return preserveOnRefresh != null;
    }

    @Override
    public String getPageTitle(WrappedRequest request,
            Class<? extends UI> uiClass) {
        Title titleAnnotation = getAnnotationFor(uiClass, Title.class);
        if (titleAnnotation == null) {
            return null;
        } else {
            return titleAnnotation.value();
        }
    }

    @Override
    public UI getExistingUI(WrappedRequest request) {
        return null;
    }
}
