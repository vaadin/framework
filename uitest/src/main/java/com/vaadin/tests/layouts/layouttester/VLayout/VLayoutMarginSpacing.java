/*
 * Copyright 2000-2020 Vaadin Ltd.
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

import java.util.Iterator;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.layouts.layouttester.BaseLayoutForSpacingMargin;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @since
 * @author Vaadin Ltd
 */

public class VLayoutMarginSpacing extends BaseLayoutForSpacingMargin {

    /**
     * @param layoutClass
     */
    public VLayoutMarginSpacing() {
        super(VerticalLayout.class);
    }

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
        Iterator<Component> iterator = l2.iterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            if (component instanceof Table) {
                component.setSizeUndefined();
            } else if (component instanceof Label) {
                component.setWidth("100%");
            }
        }
    }

    @Override
    protected void setLayoutMeasures(AbstractOrderedLayout l1,
            AbstractOrderedLayout l2, String w, String h) {
        l1.setSizeUndefined();
        l2.setSizeUndefined();
    }

}
