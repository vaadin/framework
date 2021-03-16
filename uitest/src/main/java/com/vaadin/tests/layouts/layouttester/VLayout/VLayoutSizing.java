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
package com.vaadin.tests.layouts.layouttester.VLayout;

import com.vaadin.tests.layouts.layouttester.BaseLayoutSizing;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @since
 * @author Vaadin Ltd
 */

public class VLayoutSizing extends BaseLayoutSizing {

    /**
     * @param layoutClass
     */
    public VLayoutSizing() {
        super(VerticalLayout.class);
    }

    @Override
    protected void setLayoutMeasures(AbstractOrderedLayout l1,
            AbstractOrderedLayout l2, String w, String h) {
        l1.setWidth("400px");
        l1.setHeight("-1px");
        l2.setWidth("400px");
        l2.setHeight("500px");
    }
}
