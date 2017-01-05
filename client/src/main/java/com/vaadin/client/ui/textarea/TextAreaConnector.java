/*
 * Copyright 2000-2016 Vaadin Ltd.
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

package com.vaadin.client.ui.textarea;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.vaadin.client.event.InputEvent;
import com.vaadin.client.ui.VTextArea;
import com.vaadin.client.ui.textfield.AbstractTextFieldConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.textarea.TextAreaServerRpc;
import com.vaadin.shared.ui.textarea.TextAreaState;
import com.vaadin.ui.TextArea;

@Connect(TextArea.class)
public class TextAreaConnector extends AbstractTextFieldConnector {

    @Override
    public TextAreaState getState() {
        return (TextAreaState) super.getState();
    }

    @Override
    public VTextArea getWidget() {
        return (VTextArea) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();
        getWidget().addChangeHandler(event -> sendValueChange());
        getWidget().addDomHandler(event -> {
            getValueChangeHandler().scheduleValueChange();
        }, InputEvent.getType());

        getWidget().addMouseUpHandler(new ResizeMouseUpHandler());
    }

    /*
     * Workaround to handle the resize on the mouse up.
     */
    private class ResizeMouseUpHandler implements MouseUpHandler {

        @Override
        public void onMouseUp(MouseUpEvent event) {
            Style elementStyle = getWidget().getElement().getStyle();

            String newHeight = elementStyle.getHeight();
            String newWidth = elementStyle.getWidth();

            if (newHeight == null) {
                newHeight = "";
            }
            if (newWidth == null) {
                newWidth = "";
            }

            if (!newHeight.equals(getState().height)) {
                getRpcProxy(TextAreaServerRpc.class).setHeight(newHeight);
            }
            if (!newWidth.equals(getState().width)) {
                getRpcProxy(TextAreaServerRpc.class).setWidth(newWidth);
            }
        }
    }
}
