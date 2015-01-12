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
package com.vaadin.tests.design.nested;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;

/**
 * Child design component
 * 
 * @author Vaadin Ltd
 */
@DesignRoot("mychilddesign.html")
public class MyChildDesign extends HorizontalLayout {
    public Label childLabel;
    public MyChildDesignCustomComponent childCustomComponent;

    public MyChildDesign() {
        Design.read(this);
        childLabel.setDescription("added in constructor");
    }
}
