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

package com.vaadin.tests.serialization;

import java.util.Map;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.LegacyComponent;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class LegacySerializerUI extends AbstractTestUIWithLog {

    public class LegacySerializerComponent extends AbstractComponent
            implements LegacyComponent {

        @Override
        public void changeVariables(Object source,
                Map<String, Object> variables) {
            log("doubleInfinity: " + variables.get("doubleInfinity"));
        }

        @Override
        public void paintContent(PaintTarget target) throws PaintException {
            target.addAttribute("doubleInfinity", Double.POSITIVE_INFINITY);
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new LegacySerializerComponent());
    }
}
