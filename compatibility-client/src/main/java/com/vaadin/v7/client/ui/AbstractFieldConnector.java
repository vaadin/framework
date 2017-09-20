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
package com.vaadin.v7.client.ui;

import com.google.gwt.user.client.ui.Focusable;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.HasErrorIndicator;
import com.vaadin.client.ui.HasRequiredIndicator;
import com.vaadin.v7.shared.AbstractFieldState;

@Deprecated
public abstract class AbstractFieldConnector
        extends AbstractLegacyComponentConnector
        implements HasRequiredIndicator, HasErrorIndicator {

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
     * Required indicators are hidden if the field or its data source is
     * read-only.
     * <p>
     * NOTE: since 8.0 this only delegates to
     * {@link #isRequiredIndicatorVisible()}, and is left for legacy reasons.
     * 
     * @deprecated Use {@link #isRequiredIndicatorVisible()} instead.
     *
     * @return true if required indicator should be shown
     */
    public boolean isRequired() {
        return isRequiredIndicatorVisible();
    }

    /**
     * Checks whether the required indicator should be shown for the field.
     *
     * Required indicators are hidden if the field or its data source is
     * read-only.
     *
     * @return true if required indicator should be shown
     */
    @Override
    public boolean isRequiredIndicatorVisible() {
        return getState().required && !isReadOnly();
    }

    @Override
    public boolean isErrorIndicatorVisible() {
        return super.isErrorIndicatorVisible() && !getState().hideErrors;
    }

    @Override
    protected void updateWidgetStyleNames() {
        super.updateWidgetStyleNames();

        // add / remove modified style name to Fields
        setWidgetStyleName(StyleConstants.MODIFIED, isModified());

        // add / remove error style name to Fields
        setWidgetStyleNameWithPrefix(getWidget().getStylePrimaryName(),
                StyleConstants.REQUIRED_EXT, isRequiredIndicatorVisible());

        getWidget().setStyleName(StyleConstants.REQUIRED,
                isRequiredIndicatorVisible());
    }

    @OnStateChange("tabIndex")
    void updateTabIndex() {
        // AbstractFieldState is not inheriting TabIndexState because of
        // AbstractLegacyComponentState, thus need to set tab index here
        // (instead of AbstractComponentConnector)
        if (getWidget() instanceof Focusable) {
            ((Focusable) getWidget()).setTabIndex(getState().tabIndex);
        }
    }
}
