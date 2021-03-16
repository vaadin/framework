/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.shared;

import com.vaadin.shared.ui.TabIndexState;

/**
 * Shared state for {@link com.vaadin.ui.AbstractField}.
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 *
 */
public class AbstractFieldState extends TabIndexState {
    public boolean propertyReadOnly = false;
    public boolean hideErrors = false;
    public boolean required = false;
    public boolean modified = false;

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
