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

package com.vaadin.client.ui.textarea;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.vaadin.client.Util.CssSize;
import com.vaadin.client.ui.VTextArea;
import com.vaadin.client.ui.textfield.TextFieldConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.textarea.TextAreaState;
import com.vaadin.ui.TextArea;

@Connect(TextArea.class)
public class TextAreaConnector extends TextFieldConnector {

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

        getWidget().addMouseUpHandler(new ResizeMouseUpHandler());
    }

    /*
     * Workaround to handle the resize on the mouse up.
     */
    private class ResizeMouseUpHandler implements MouseUpHandler {

        @Override
        public void onMouseUp(MouseUpEvent event) {
            Element element = getWidget().getElement();

            updateSize(element.getStyle().getHeight(), getState().height,
                    "height");
            updateSize(element.getStyle().getWidth(), getState().width, "width");
        }

        /*
         * Update the specified size on the server.
         */
        private void updateSize(String sizeText, String stateSizeText,
                String sizeType) {

            CssSize stateSize = CssSize.fromString(stateSizeText);
            CssSize newSize = CssSize.fromString(sizeText);

            if (stateSize == null && newSize == null) {
                return;

            } else if (newSize == null) {
                sizeText = "";

                // Else, if the current stateSize is null, just go ahead and set
                // the newSize, so no check on stateSize is needed.

            } else if (stateSize != null && stateSize.equals(newSize)) {
                return;
            }

            getConnection().updateVariable(getConnectorId(), sizeType,
                    sizeText, false);
        }

    }

}
