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

package com.vaadin.server;

import java.io.Serializable;

/**
 * An interface describing the widgetset that the client should try to load.
 * <p>
 * In addition to explicit use within the framework, adding a class called
 * AppWidgetset implementing this interface in the default package will
 * configure the widgetset to use unless the user has explicitly selected a
 * different widgetset. See {@link BootstrapHandler} and {@link UIProvider} for
 * more information.
 *
 * @since 7.7
 */
public interface WidgetsetInfo extends Serializable {

    /**
     * Returns the name of the widgetset to use.
     *
     * @return widgetset name
     */
    public String getWidgetsetName();

    /**
     * Returns the widgetset URL. Can be null for local widgetsets at default
     * location.
     *
     * @return widgetset URL or null for client generated URL
     */
    public String getWidgetsetUrl();

    /**
     * If cdn is true, the client side should wait if it didn't manage to load
     * the widgetset, as it might still be compiling.
     *
     * @return true to wait and retry if the widgetset could not be loaded
     */
    public boolean isCdn();

}
