/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class LayoutManagerIE8 extends LayoutManager {

    private Map<Element, MeasuredSize> measuredSizes = new HashMap<Element, MeasuredSize>();

    @Override
    protected void setMeasuredSize(Element element, MeasuredSize measuredSize) {
        if (measuredSize != null) {
            measuredSizes.put(element, measuredSize);
        } else {
            measuredSizes.remove(element);
        }
    }

    @Override
    protected MeasuredSize getMeasuredSize(Element element,
            MeasuredSize defaultSize) {
        MeasuredSize measured = measuredSizes.get(element);
        if (measured != null) {
            return measured;
        } else {
            return defaultSize;
        }
    }

    @Override
    protected void cleanMeasuredSizes() {
        Document document = RootPanel.get().getElement().getOwnerDocument();

        Iterator<Element> i = measuredSizes.keySet().iterator();
        while (i.hasNext()) {
            Element e = i.next();
            if (e.getOwnerDocument() != document) {
                i.remove();
            }
        }
    }
}
