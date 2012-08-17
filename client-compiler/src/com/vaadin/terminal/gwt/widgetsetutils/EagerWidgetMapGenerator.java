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
package com.vaadin.terminal.gwt.widgetsetutils;

import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.terminal.gwt.client.ServerConnector;

/**
 * WidgetMap generator that builds a widgetset that packs all included widgets
 * into a single JavaScript file loaded at application initialization. Initially
 * loaded data will be relatively large, but minimal amount of server requests
 * will be done.
 * <p>
 * This is the default generator in version 6.4 and produces similar type of
 * widgetset as in previous versions of Vaadin. To activate "code splitting",
 * use the {@link WidgetMapGenerator} instead, that loads most components
 * deferred.
 * 
 * @see WidgetMapGenerator
 * 
 */
public class EagerWidgetMapGenerator extends WidgetMapGenerator {

    @Override
    protected LoadStyle getLoadStyle(Class<? extends ServerConnector> connector) {
        return LoadStyle.EAGER;
    }
}
