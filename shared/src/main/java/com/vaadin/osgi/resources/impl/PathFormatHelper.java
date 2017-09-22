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
package com.vaadin.osgi.resources.impl;

/**
 * Helper for formatting the Alias, and Theme and Widgetset names.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.1
 */
public final class PathFormatHelper {
    private static final String VAADIN_ROOT_ALIAS_FORMAT = "/%s/VAADIN/%s";
    private static final String VAADIN_ROOT_FORMAT = "/VAADIN/%s";

    private static final String VAADIN_THEME_ALIAS_FORMAT = "/%s/VAADIN/themes/%s";
    private static final String VAADIN_WIDGETSET_ALIAS_FORMAT = "/%s/VAADIN/widgetsets/%s";

    private static final String VAADIN_THEME_PATH_FORMAT = "/VAADIN/themes/%s";
    private static final String VAADIN_WIDGETSET_PATH_FORMAT = "/VAADIN/widgetsets/%s";

    private PathFormatHelper() {

    }

    /**
     * Returns the alias for the theme given a the theme name and a path prefix.
     *
     * @param themeName
     *            the theme name
     * @param pathPrefix
     *            the prefix for the /VAADIN/ folder
     * @return the alias
     */
    public static String getThemeAlias(String themeName, String pathPrefix) {
        return String.format(VAADIN_THEME_ALIAS_FORMAT, pathPrefix, themeName);
    }

    /**
     * Returns the expected/default path of the theme folder in the source
     * bundle.
     *
     * @param themeName
     *            the name of the theme
     * @return the path of the theme folder in the source bundle
     */
    public static String getThemePath(String themeName) {
        return String.format(VAADIN_THEME_PATH_FORMAT, themeName);
    }

    /**
     * Returns the alias for a widgetset given a the widgetset name and a path
     * prefix.
     *
     * @param widgetsetName
     *            the name of the widgetset
     * @param pathPrefix
     *            the prefix for the /VAADIN/ folder
     * @return the alias
     */
    public static String getWidgetsetAlias(String widgetsetName,
            String pathPrefix) {
        return String.format(VAADIN_WIDGETSET_ALIAS_FORMAT, pathPrefix,
                widgetsetName);
    }

    /**
     * Returns the expected/default path of the widgetset folder in the source
     * bundle.
     *
     * @param widgetsetName
     *            the name of the widgetset
     * @return the path of the widgetset folder in the source bundle
     */
    public static String getWidgetsetPath(String widgetsetName) {
        return String.format(VAADIN_WIDGETSET_PATH_FORMAT, widgetsetName);
    }

    /**
     * Returns the alias for a resource that will placed under the /VAADIN/
     * folder.
     *
     * @param resourceName
     *            the name of the resource
     * @param pathPrefix
     *            the prefix for the /VAADIN/ folder
     * @return the alias
     */
    public static String getRootResourceAlias(String resourceName,
            String pathPrefix) {
        return String.format(VAADIN_ROOT_ALIAS_FORMAT, pathPrefix,
                resourceName);
    }

    /**
     * Returns the expected/default path of the resource in the source bundle.
     *
     * @param resourceName
     *            the name of the resource
     * @return the path of the resource in the source bundle
     */
    public static String getRootResourcePath(String resourceName) {
        return String.format(VAADIN_ROOT_FORMAT, resourceName);
    }
}
