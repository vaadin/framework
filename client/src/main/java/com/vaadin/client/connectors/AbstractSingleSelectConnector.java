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
package com.vaadin.client.connectors;

import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.HasRequiredIndicator;
import com.vaadin.shared.ui.AbstractSingleSelectState;

/**
 * An abstract class for single selection connectors.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public abstract class AbstractSingleSelectConnector<WIDGET extends Widget & HasAllFocusHandlers>
        extends AbstractFocusableListingConnector<WIDGET>
        implements HasRequiredIndicator {

    @Override
    public AbstractSingleSelectState getState() {
        return (AbstractSingleSelectState) super.getState();
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return getState().required && !isReadOnly();
    }
}
