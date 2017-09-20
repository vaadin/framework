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
package com.vaadin.shared.ui.listselect;

import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.ui.abstractmultiselect.AbstractMultiSelectState;

/**
 * Shared state for ListSelect component.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class ListSelectState extends AbstractMultiSelectState {
    public static final String PRIMARY_STYLENAME = "v-select";
    {
        primaryStyleName = PRIMARY_STYLENAME;
    }
    @DelegateToWidget
    public int rows;
}
