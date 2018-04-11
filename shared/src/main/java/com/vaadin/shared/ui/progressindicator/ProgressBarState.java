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

package com.vaadin.shared.ui.progressindicator;

import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.SharedState;

/**
 * {@link SharedState} for {@link com.vaadin.ui.ProgressBar}.
 *
 * @since 7.1
 * @author Vaadin Ltd
 */
public class ProgressBarState extends AbstractFieldState {
    public static final String PRIMARY_STYLE_NAME = "v-progressbar";

    {
        primaryStyleName = PRIMARY_STYLE_NAME;
    }
    public boolean indeterminate = false;
    @NoLayout
    public float state = 0.0f;

}
