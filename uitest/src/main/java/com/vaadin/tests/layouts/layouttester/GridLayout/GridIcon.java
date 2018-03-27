/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class GridIcon extends GridBaseLayoutTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        layout.addComponent(createLabelsFields(TextField.class, true, ""));
        layout.addComponent(createLabelsFields(Label.class, true, ""));
        layout.addComponent(createLabelsFields(Button.class, true, ""));
        layout.addComponent(createLabelsFields(ComboBox.class, true, ""));
        layout.addComponent(createLabelsFields(Link.class, true, ""));
        layout.addComponent(createLabelsFields(TabSheet.class, true, ""));
        super.setup(request);
    }
}
