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
package com.vaadin.v7.client.ui.label;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.PreElement;
import com.vaadin.client.Profiler;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.v7.client.ui.AbstractLegacyComponentConnector;
import com.vaadin.v7.client.ui.VLabel;
import com.vaadin.v7.shared.ui.label.LabelState;
import com.vaadin.v7.ui.Label;

@Connect(value = Label.class, loadStyle = LoadStyle.EAGER)
public class LabelConnector extends AbstractLegacyComponentConnector {

    @Override
    public LabelState getState() {
        return (LabelState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        boolean sinkOnloads = false;
        Profiler.enter("LabelConnector.onStateChanged update content");
        VLabel widget = getWidget();
        switch (getState().contentMode) {
        case PREFORMATTED:
            PreElement preElement = Document.get().createPreElement();
            preElement.setInnerText(getState().text);
            // clear existing content
            widget.setHTML("");
            // add preformatted text to dom
            widget.getElement().appendChild(preElement);
            break;

        case TEXT:
            widget.setText(getState().text);
            break;

        case HTML:
        case RAW:
            sinkOnloads = true;
        case XML:
            widget.setHTML(getState().text);
            break;
        default:
            widget.setText("");
            break;

        }
        Profiler.leave("LabelConnector.onStateChanged update content");

        if (sinkOnloads) {
            Profiler.enter("LabelConnector.onStateChanged sinkOnloads");
            WidgetUtil.sinkOnloadForImages(widget.getElement());
            Profiler.leave("LabelConnector.onStateChanged sinkOnloads");
        }
    }

    @Override
    public VLabel getWidget() {
        return (VLabel) super.getWidget();
    }

}
