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
package com.vaadin.shared.tokka.ui.components.fields;

import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.ui.Orientation;

public class SliderState extends AbstractFieldState {
    {
        primaryStyleName = "v-slider";
    }

    @DelegateToWidget
    @NoLayout
    public double maxValue = 100.0;
    @DelegateToWidget
    @NoLayout
    public double minValue = 0.0;

    @NoLayout
    public double value = minValue;

    /**
     * The number of fractional digits that are considered significant. Must be
     * non-negative.
     */
    @DelegateToWidget
    @NoLayout
    public int resolution = 0;

    @DelegateToWidget
    public Orientation orientation = Orientation.HORIZONTAL;

}
