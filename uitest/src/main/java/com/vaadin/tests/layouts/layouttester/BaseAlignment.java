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
package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractOrderedLayout;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
abstract public class BaseAlignment extends BaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // create two columns of components with different
        // alignment. Used to test alignment in layouts
        init();
        buildLayout();
        super.setup(request);
    }

    public BaseAlignment(Class<? extends AbstractOrderedLayout> layoutClass) {
        super(layoutClass);
    }

    /**
     * Build Layout for test
     */
    private void buildLayout() {
        for (int i = 0; i < components.length; i++) {
            AbstractOrderedLayout layout = null;
            try {
                layout = (AbstractOrderedLayout) layoutClass.newInstance();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            layout.setHeight("100px");
            layout.setWidth("200px");
            layout.addComponent(components[i]);
            layout.setComponentAlignment(components[i], alignments[i]);
            if (i < components.length / 2) {
                l1.addComponent(layout);
            } else {
                l2.addComponent(layout);
            }
        }
    }
}
