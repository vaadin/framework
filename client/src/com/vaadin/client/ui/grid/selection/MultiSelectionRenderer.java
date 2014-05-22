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
package com.vaadin.client.ui.grid.selection;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.ui.grid.FlyweightCell;
import com.vaadin.client.ui.grid.Renderer;

/* This class will probably not survive the final merge of all selection functionality. */
public class MultiSelectionRenderer implements Renderer<Boolean> {
    @Override
    public void render(FlyweightCell cell, Boolean data) {
        Element checkbox = Element.as(DOM.createInputCheck());
        if (Boolean.TRUE.equals(data)) {
            checkbox.setAttribute("checked", "checked");
        }
        cell.getElement().removeAllChildren();
        cell.getElement().appendChild(checkbox);
    }
}
