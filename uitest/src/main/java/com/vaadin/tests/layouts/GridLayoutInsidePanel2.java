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
package com.vaadin.tests.layouts;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;

public class GridLayoutInsidePanel2 extends LegacyApplication {

    private Layout layout;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow("Main");
        setMainWindow(w);
        layout = (Layout) w.getContent();
        GridLayout gl = new GridLayout(1, 1);
        gl.setSizeUndefined();
        Label l = new Label("This should be visible");
        l.setWidth("100px");
        gl.addComponent(l);

        layout.setSizeUndefined();
        layout.addComponent(gl);
    }

}
