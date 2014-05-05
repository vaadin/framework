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
package com.vaadin.client.ui;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.shared.AbstractFieldState;

public abstract class AbstractFieldConnector extends AbstractComponentConnector {

    @Override
    public AbstractFieldState getState() {
        return (AbstractFieldState) super.getState();
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly() || getState().propertyReadOnly;
    }

    public boolean isModified() {
        return getState().modified;
    }

    /**
     * Checks whether the required indicator should be shown for the field.
     * 
     * Required indicators are hidden if the field or its data source is
     * read-only.
     * 
     * @return true if required indicator should be shown
     */
    public boolean isRequired() {
        return getState().required && !isReadOnly();
    }

    @Override
    protected void updateWidgetStyleNames() {
        super.updateWidgetStyleNames();

        // add / remove modified style name to Fields
        setWidgetStyleName(ApplicationConnection.MODIFIED_CLASSNAME,
                isModified());

        // add / remove error style name to Fields
        setWidgetStyleNameWithPrefix(getWidget().getStylePrimaryName(),
                ApplicationConnection.REQUIRED_CLASSNAME_EXT, isRequired());
    }
}
