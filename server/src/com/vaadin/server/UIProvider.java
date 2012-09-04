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

import com.vaadin.Application;
import com.vaadin.annotations.Widgetset;
import com.vaadin.ui.UI;

public interface UIProvider {
    public Class<? extends UI> getUIClass(Application application,
            WrappedRequest request);

    public UI createInstance(Application application, Class<? extends UI> type,
            WrappedRequest request);

    public String getPageTitleForUI(WrappedRequest request,
            Class<? extends UI> uiClass);

    /**
     * Checks whether the same UI state should be reused if the framework can
     * detect that the application is opened in a browser window where it has
     * previously been open. The framework attempts to discover this by checking
     * the value of window.name in the browser.
     * 
     * @param request
     * @param uiClass
     * 
     * @return <code>true</code>if the same UI instance should be reused e.g.
     *         when the browser window is refreshed.
     */
    public boolean isUiPreserved(WrappedRequest request,
            Class<? extends UI> uiClass);

    /**
     * Finds the widgetset to use for a specific UI. If no specific widgetset is
     * required, <code>null</code> is returned.
     * <p>
     * The default implementation uses the @{@link Widgetset} annotation if it's
     * defined for the UI class.
     * 
     * @param request
     *            the wrapped request for which to get a widgetset
     * @param uiClass
     *            the UI class to get a widgetset for
     * @return the name of the widgetset, or <code>null</code> if the default
     *         widgetset should be used
     * 
     */
    public String getWidgetsetForUI(WrappedRequest request,
            Class<? extends UI> uiClass);

    /**
     * Finds the theme to use for a specific UI. If no specific theme is
     * required, <code>null</code> is returned.
     * 
     * TODO Tell what the default implementation does once it does something.
     * 
     * @param uI
     *            the UI to get a theme for
     * @return the name of the theme, or <code>null</code> if the default theme
     *         should be used
     * 
     */
    public String getThemeForUI(WrappedRequest request,
            Class<? extends UI> uiClass);

}
