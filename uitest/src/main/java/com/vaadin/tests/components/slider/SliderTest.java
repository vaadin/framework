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
package com.vaadin.tests.components.slider;

import java.util.LinkedHashMap;

import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.ui.Slider;

public class SliderTest extends AbstractFieldTest<Slider, Double> {

    private Command<Slider, Double> minCommand = new Command<Slider, Double>() {
        @Override
        public void execute(Slider c, Double value, Object data) {
            c.setMin(value);
        }
    };

    private Command<Slider, Double> maxCommand = new Command<Slider, Double>() {
        @Override
        public void execute(Slider c, Double value, Object data) {
            c.setMax(value);
        }
    };

    private Command<Slider, SliderOrientation> orientationCommand = new Command<Slider, SliderOrientation>() {
        @Override
        public void execute(Slider c, SliderOrientation value, Object data) {
            c.setOrientation(value);
        }
    };
    private Command<Slider, Integer> resolutionCommand = new Command<Slider, Integer>() {
        @Override
        public void execute(Slider c, Integer value, Object data) {
            c.setResolution(value);
        }
    };

    @Override
    protected Class<Slider> getTestClass() {
        return Slider.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

        createMinSelect(CATEGORY_FEATURES);
        createMaxSelect(CATEGORY_FEATURES);
        createResolutionSelect(CATEGORY_FEATURES);
        createOrientationSelect(CATEGORY_FEATURES);
    }

    private void createResolutionSelect(String category) {
        createSelectAction("Resolution", category, createIntegerOptions(10),
                "1", resolutionCommand);

    }

    private void createOrientationSelect(String category) {
        LinkedHashMap<String, SliderOrientation> options = new LinkedHashMap<>();
        options.put("Horizontal", SliderOrientation.HORIZONTAL);
        options.put("Vertical", SliderOrientation.VERTICAL);
        createSelectAction("Orientation", category, options, "Horizontal",
                orientationCommand);

    }

    private void createMaxSelect(String category) {
        createSelectAction("Max", category, createDoubleOptions(100), "0",
                maxCommand);
    }

    private void createMinSelect(String category) {
        createSelectAction("Min", category, createDoubleOptions(100), "0",
                minCommand);

    }

}
