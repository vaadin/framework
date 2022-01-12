/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.tests.components.optiongroup;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.OptionGroup;

public class OptionGroupDisabled extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.setEnabled(false);
        optionGroup.setImmediate(true);
        optionGroup.setMultiSelect(true);
        optionGroup.addItem("test 1");
        optionGroup.addItem("test 2");
        optionGroup.addItem("test 3");

        addComponent(optionGroup);
    }

}
