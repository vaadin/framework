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
package com.vaadin.shared.ui.window;

import com.vaadin.shared.Connector;
import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.ui.panel.PanelState;

public class WindowState extends PanelState {
    {
        primaryStyleName = "v-window";
    }

    @NoLayout
    public boolean modal = false;
    @NoLayout
    public boolean resizable = true;
    @NoLayout
    public boolean resizeLazy = false;
    @NoLayout
    public boolean draggable = true;
    @NoLayout
    public boolean centered = false;
    @NoLayout
    public int positionX = -1;
    @NoLayout
    public int positionY = -1;
    public WindowMode windowMode = WindowMode.NORMAL;

    @NoLayout
    public String assistivePrefix = "";
    @NoLayout
    public String assistivePostfix = "";
    @NoLayout
    public Connector[] contentDescription = new Connector[0];
    @NoLayout
    public WindowRole role = WindowRole.DIALOG;
    @NoLayout
    public boolean assistiveTabStop = false;
    @NoLayout
    public String assistiveTabStopTopText = "Top of dialog";
    @NoLayout
    public String assistiveTabStopBottomText = "Bottom of Dialog";
}
