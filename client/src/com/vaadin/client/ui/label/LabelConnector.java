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
package com.vaadin.client.ui.label;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.PreElement;
import com.vaadin.client.Profiler;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.VLabel;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.label.LabelState;
import com.vaadin.ui.Label;

@Connect(value = Label.class, loadStyle = LoadStyle.EAGER)
public class LabelConnector extends AbstractComponentConnector {

    @Override
    public LabelState getState() {
        return (LabelState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        boolean sinkOnloads = false;
        Profiler.enter("LabelConnector.onStateChanged update content");
        switch (getState().contentMode) {
        case PREFORMATTED:
            PreElement preElement = Document.get().createPreElement();
            preElement.setInnerText(getState().text);
            // clear existing content
            getWidget().setHTML("");
            // add preformatted text to dom
            getWidget().getElement().appendChild(preElement);
            break;

        case TEXT:
            getWidget().setText(getState().text);
            break;

        case HTML:
        case RAW:
            sinkOnloads = true;
        case XML:
            getWidget().setHTML(getState().text);
            break;
        default:
            getWidget().setText("");
            break;

        }
        Profiler.leave("LabelConnector.onStateChanged update content");

        if (sinkOnloads) {
            Profiler.enter("LabelConnector.onStateChanged sinkOnloads");
            WidgetUtil.sinkOnloadForImages(getWidget().getElement());
            Profiler.leave("LabelConnector.onStateChanged sinkOnloads");
        }
    }

    @Override
    public VLabel getWidget() {
        return (VLabel) super.getWidget();
    }

}
