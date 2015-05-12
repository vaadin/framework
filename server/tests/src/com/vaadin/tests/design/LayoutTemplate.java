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
package com.vaadin.tests.design;

import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;

/**
 * Template to be populated in the tests
 * 
 * @since
 * @author Vaadin Ltd
 */
public class LayoutTemplate extends VerticalLayout {
    private NativeButton firstButton; // assigned based on local id
    private NativeButton secondButton; // assigned based on id
    private NativeButton yetanotherbutton; // assigned based on caption
    private Button clickme; // assigned based on caption

    public NativeButton getFirstButton() {
        return firstButton;
    }

    public NativeButton getSecondButton() {
        return secondButton;
    }

    public NativeButton getYetanotherbutton() {
        return yetanotherbutton;
    }

    public Button getClickme() {
        return clickme;
    }
}
