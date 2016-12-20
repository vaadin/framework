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
package com.vaadin.ui;

import java.util.EnumSet;

/**
 * Option group test from Book of Vaadin
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class RadioButtonGroupBoVTest {
    public enum Status {
        STATE_A, STATE_B, STATE_C, STATE_D;

        public String getCaption() {
            return "** " + toString();
        }
    }

    public void createOptionGroup() {
        RadioButtonGroup<Status> s = new RadioButtonGroup<>();
        s.setItems(EnumSet.allOf(Status.class));
        s.setItemCaptionGenerator(Status::getCaption);
    }

}
