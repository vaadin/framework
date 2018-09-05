/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.shared;

import com.vaadin.shared.Connector;
import com.vaadin.shared.annotations.NoLayout;

/**
 * Shared state for {@link com.vaadin.ui.AbstractField}.
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 *
 */
public class AbstractFieldState extends AbstractLegacyComponentState {
    public boolean propertyReadOnly = false;
    public boolean hideErrors = false;
    public boolean required = false;
    public boolean modified = false;
    /**
     * The <i>tabulator index</i> of the field.
     */
    @NoLayout
    public int tabIndex = 0;

    /**
     * The component which should receive focus events instead of the custom
     * field wrapper.
     * <p>
     * This is not used in all fields, but needs to be here for the time being
     * (#20468).
     *
     * @since 7.7.5
     */
    public Connector focusDelegate;
}
