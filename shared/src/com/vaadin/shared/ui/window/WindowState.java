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
import com.vaadin.shared.ui.panel.PanelState;

public class WindowState extends PanelState {
    {
        primaryStyleName = "v-window";
    }

    public boolean modal = false;
    public boolean resizable = true;
    public boolean resizeLazy = false;
    public boolean draggable = true;
    public boolean centered = false;
    public int positionX = -1;
    public int positionY = -1;
    public WindowMode windowMode = WindowMode.NORMAL;

    public String assistivePrefix = "";
    public String assistivePostfix = "";
    public Connector[] contentDescription = new Connector[0];
    public WindowRole role = WindowRole.DIALOG;
    public boolean assistiveTabStop = false;
    public String assistiveTabStopTopText = "Top of dialog";
    public String assistiveTabStopBottomText = "Bottom of Dialog";
}
