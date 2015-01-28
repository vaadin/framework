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
package com.vaadin.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Alternative MeasuredSize storage for IE8. Storing any information in a DOM
 * element in IE8 seems to make the browser think the element has changed in a
 * way that requires a reflow. To work around that, the MeasureData is instead
 * stored in Map for IE8.
 * 
 * This implementation is injected for IE8 by a replace-with definition in the
 * GWT module.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class LayoutManagerIE8 extends LayoutManager {

    private Map<Element, MeasuredSize> measuredSizes = new HashMap<Element, MeasuredSize>();

    // this method is needed to test for memory leaks (see
    // LayoutMemoryUsageIE8ExtensionConnector) but can be private
    private int getMeasuredSizesMapSize() {
        return measuredSizes.size();
    }

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
        Profiler.enter("LayoutManager.cleanMeasuredSizes");

        // #12688: IE8 was leaking memory when adding&removing components.
        // Uses IE specific logic to figure if an element has been removed from
        // DOM or not. For removed elements the measured size is discarded.
        Node rootNode = Document.get().getBody();

        Iterator<Element> i = measuredSizes.keySet().iterator();
        while (i.hasNext()) {
            Element e = i.next();
            if (!rootNode.isOrHasChild(e)) {
                i.remove();
            }
        }

        Profiler.leave("LayoutManager.cleanMeasuredSizes");
    }

    @Override
    protected void performBrowserLayoutHacks() {
        Profiler.enter("LayoutManagerIE8.performBrowserLayoutHacks");
        /*
         * Fixes IE8 issues where IE8 sometimes forgets to update the size of
         * the containing element. To force a reflow by modifying the magical
         * zoom property.
         */
        WidgetUtil.forceIE8Redraw(RootPanel.get().getElement());
        Profiler.leave("LayoutManagerIE8.performBrowserLayoutHacks");
    }
}
