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
package com.vaadin.tests.components.grid;

import com.vaadin.annotations.JavaScript;
import com.vaadin.tests.components.grid.JavaScriptRenderers.MyBean;
import com.vaadin.ui.renderers.AbstractJavaScriptRenderer;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
@JavaScript("myBeanJsRenderer.js")
public class MyBeanJSRenderer extends AbstractJavaScriptRenderer<MyBean> {

    public MyBeanJSRenderer() {
        super(MyBean.class, "");
    }

}
