/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.UI;

public abstract class UIProvider implements Serializable {

    /* Default widgetset name to look for */
    private static final String APP_WIDGETSET_NAME = "AppWidgetset";

    public abstract Class<? extends UI> getUIClass(UIClassSelectionEvent event);

    public UI createInstance(UICreateEvent event) {
        try {
            return event.getUIClass().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not instantiate UI class", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not access UI class", e);
        }
    }

    /**
     * Helper to get an annotation for a class. If the annotation is not present
     * on the target class, its super classes and directly implemented
     * interfaces are also searched for the annotation. Interfaces implemented
     * by superclasses are not taken into account.
     * <p>
     * Note that searching implemented interfaces for {@code @Inherited}
     * annotations and searching for superclasses for non-inherited annotations
     * do not follow the standard semantics and are supported for backwards
     * compatibility. Future versions of the framework might only support the
     * standard semantics of {@code @Inherited}.
     *
     * @param clazz
     *            the class from which the annotation should be found
     * @param annotationType
     *            the annotation type to look for
     * @return an annotation of the given type, or <code>null</code> if the
     *         annotation is not present on the class
     */
    protected static <T extends Annotation> T getAnnotationFor(Class<?> clazz,
            Class<T> annotationType) {
        // Don't discover hierarchy if annotation is inherited
        if (annotationType.getAnnotation(Inherited.class) != null) {
            T annotation = clazz.getAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            }
        } else {
            // Find from the class hierarchy
            Class<?> currentType = clazz;
            while (currentType != Object.class) {
                T annotation = currentType.getAnnotation(annotationType);
                if (annotation != null) {
                    return annotation;
                } else {
                    currentType = currentType.getSuperclass();
                }
            }
        }

        // Find from a directly implemented interface
        for (Class<?> iface : clazz.getInterfaces()) {
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
        Theme uiTheme = getAnnotationFor(event.getUIClass(), Theme.class);
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
     * This method uses the Widgetset definition priority order from
     * {@link #getWidgetsetInfo(UICreateEvent)}.
     * <p>
     * <strong>Note:</strong> This method exists only for backwards
     * compatibility and overriding it won't have the effect it used to.
     *
     * @param event
     *            the UI create event with information about the UI and the
     *            current request.
     * @return the name of the widgetset, or <code>null</code> if the default
     *         widgetset should be used
     * @deprecated This method has been replaced by
     *             {@link #getWidgetsetInfo(UICreateEvent)} in 7.7
     */
    @Deprecated
    public String getWidgetset(UICreateEvent event) {
        WidgetsetInfo widgetsetInfo = getWidgetsetInfo(event);
        return widgetsetInfo != null ? widgetsetInfo.getWidgetsetName() : null;
    }

    /**
     * Finds the widgetset to use for a specific UI. If no specific widgetset is
     * required, <code>null</code> is returned.
     * <p>
     * The default implementation uses the following order of priority for
     * finding the widgetset information:
     * <ul>
     * <li>@{@link Widgetset} annotation if it is defined for the UI class</li>
     * <li>The class AppWidgetset if one exists in the default package</li>
     * <li>A widgetset called AppWidgetset if there is an AppWidgetset.gwt.xml
     * file in the default package</li>
     * <li>null to use the default widgetset otherwise</li>
     * </ul>
     *
     * @since 7.7
     *
     * @param event
     *            the UI create event with information about the UI and the
     *            current request.
     * @return the widgetset info, or <code>null</code> if the default widgetset
     *         should be used
     */
    public WidgetsetInfo getWidgetsetInfo(UICreateEvent event) {
        Widgetset uiWidgetset = getAnnotationFor(event.getUIClass(),
                Widgetset.class);

        // First case: We have an @Widgetset annotation, use that
        if (uiWidgetset != null) {
            return new WidgetsetInfoImpl(uiWidgetset.value());
        }

        // Find the class AppWidgetset in the default package if one exists
        WidgetsetInfo info = getWidgetsetClassInfo();

        // Second case: we have a generated class called APP_WIDGETSET_NAME
        if (info != null) {
            return info;
        }

        // third case: we have an AppWidgetset.gwt.xml file
        else {
            InputStream resource = event.getUIClass().getResourceAsStream(
                    "/" + APP_WIDGETSET_NAME + ".gwt.xml");
            if (resource != null) {
                return new WidgetsetInfoImpl(false, null, APP_WIDGETSET_NAME);
            }
        }

        // fourth case: we are using the default widgetset
        return null;
    }

    private Class<WidgetsetInfo> findWidgetsetClass() {
        try {
            // We cannot naively use Class.forname without getting the correct
            // classloader
            // FIXME This might still fail with osgi
            ClassLoader tccl = VaadinService.getCurrent().getClassLoader();
            Class<?> c = Class.forName(APP_WIDGETSET_NAME, true, tccl);

            // if not implementing the interface, possibly a @WebListener class
            // from an earlier version - ignore it
            if (WidgetsetInfo.class.isAssignableFrom(c)) {
                return (Class<WidgetsetInfo>) c;
            }
        } catch (ClassNotFoundException e) {
            // ClassNotFound is a normal case
        }
        return null;
    }

    private WidgetsetInfo getWidgetsetClassInfo() {
        Class<WidgetsetInfo> cls = findWidgetsetClass();
        if (cls != null) {
            try {
                return cls.newInstance();
            } catch (InstantiationException e) {
                getLogger().log(
                        Level.INFO,
                        "Unexpected trying to instantiate class "
                                + cls.getName(), e);
            } catch (IllegalAccessException e) {
                getLogger()
                        .log(Level.INFO,
                                "Unexpected trying to access class "
                                        + cls.getName(), e);
            }
        }
        return null;
    }

    /**
     * Checks whether the same UI state should be reused if the framework can
     * detect that the application is opened in a browser window where it has
     * previously been open. The framework attempts to discover this by checking
     * the value of window.name in the browser.
     * <p>
     * Whenever a preserved UI is reused, its
     * {@link UI#refresh(com.vaadin.server.VaadinRequest) refresh} method is
     * invoked by the framework first.
     *
     *
     * @param event
     *            the UI create event with information about the UI and the
     *            current request.
     *
     * @return <code>true</code>if the same UI instance should be reused e.g.
     *         when the browser window is refreshed.
     */
    public boolean isPreservedOnRefresh(UICreateEvent event) {
        PreserveOnRefresh preserveOnRefresh = getAnnotationFor(
                event.getUIClass(), PreserveOnRefresh.class);
        return preserveOnRefresh != null;
    }

    public String getPageTitle(UICreateEvent event) {
        Title titleAnnotation = getAnnotationFor(event.getUIClass(),
                Title.class);
        if (titleAnnotation == null) {
            return null;
        } else {
            return titleAnnotation.value();
        }
    }

    /**
     * Finds the {@link PushMode} to use for a specific UI. If no specific push
     * mode is required, <code>null</code> is returned.
     * <p>
     * The default implementation uses the @{@link Push} annotation if it's
     * defined for the UI class.
     *
     * @param event
     *            the UI create event with information about the UI and the
     *            current request.
     * @return the push mode to use, or <code>null</code> if the default push
     *         mode should be used
     *
     */
    public PushMode getPushMode(UICreateEvent event) {
        Push push = getAnnotationFor(event.getUIClass(), Push.class);
        if (push == null) {
            return null;
        } else {
            return push.value();
        }
    }

    /**
     * Finds the {@link Transport} to use for a specific UI. If no transport is
     * defined, <code>null</code> is returned.
     * <p>
     * The default implementation uses the @{@link Push} annotation if it's
     * defined for the UI class.
     *
     * @param event
     *            the UI create event with information about the UI and the
     *            current request.
     * @return the transport type to use, or <code>null</code> if the default
     *         transport type should be used
     */
    public Transport getPushTransport(UICreateEvent event) {
        Push push = getAnnotationFor(event.getUIClass(), Push.class);
        if (push == null) {
            return null;
        } else {
            return push.transport();
        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(UIProvider.class.getName());
    }

}
