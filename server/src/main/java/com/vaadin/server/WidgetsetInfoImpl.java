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

/**
 * Default implementation of {@link WidgetsetInfo} that is used for internal
 * communication between the parts of the framework.
 * <p>
 * Class needs to be static so that it can be easily used in e.g.
 * BootstrapHandler.
 * <p>
 * This class is intended primarily for internal use. It is recommended to
 * implement WidgetsetInfo directly rather than extending or using this
 * class outside the framework, and this class is subject to changes.
 *
 * @since 7.7
 */
class WidgetsetInfoImpl implements WidgetsetInfo {

    private final boolean cdn;
    private final String widgetsetUrl;
    private final String widgetsetName;

    public WidgetsetInfoImpl(boolean cdn, String widgetsetUrl,
            String widgetsetName) {

        this.cdn = cdn;
        this.widgetsetUrl = widgetsetUrl;
        this.widgetsetName = widgetsetName;
    }

    public WidgetsetInfoImpl(String widgetsetName) {
        this(false, null, widgetsetName);
    }

    @Override
    public boolean isCdn() {
        return cdn;
    }

    @Override
    public String getWidgetsetUrl() {
        return widgetsetUrl;
    }

    @Override
    public String getWidgetsetName() {
        return widgetsetName;
    }

}