/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.ui;

import com.vaadin.client.StyleConstants;
import com.vaadin.shared.AbstractFieldState;

/**
 * Base class for field connectors.
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractFieldConnector extends AbstractComponentConnector
        implements HasRequiredIndicator {

    @Override
    public AbstractFieldState getState() {
        return (AbstractFieldState) super.getState();
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return getState().required && !isReadOnly();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void updateWidgetStyleNames() {
        super.updateWidgetStyleNames();

        // add / remove read-only style name
        setWidgetStyleName("v-readonly", isReadOnly());

        // add / remove error style name to Fields
        setWidgetStyleNameWithPrefix(getWidget().getStylePrimaryName(),
                StyleConstants.REQUIRED_EXT, isRequiredIndicatorVisible());
    }

    /**
     * Checks if the connector is read only.
     *
     * @return true
     */
    public boolean isReadOnly() {
        return getState().readOnly;
    }
}
