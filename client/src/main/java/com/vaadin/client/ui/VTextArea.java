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

package com.vaadin.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.dd.DragImageModifier;

/**
 * This class represents a multiline textfield (textarea).
 *
 * @author Vaadin Ltd.
 *
 */
public class VTextArea extends VTextField implements DragImageModifier {

    public static final String CLASSNAME = "v-textarea";

    private EnterDownHandler enterDownHandler = new EnterDownHandler();
    private boolean wordWrap = true;

    private class EnterDownHandler implements KeyDownHandler {
        @Override
        public void onKeyDown(KeyDownEvent event) {
            // Fix for #12424/13811 - if the key being pressed is enter, we stop
            // propagation of the KeyDownEvents if there were no modifier keys
            // also pressed. This prevents shortcuts that are bound to only the
            // enter key from being processed but allows usage of e.g.
            // shift-enter or ctrl-enter.
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
                    && !event.isAnyModifierKeyDown()) {
                event.stopPropagation();
            }
        }
    }

    public VTextArea() {
        super(DOM.createTextArea());
        setStyleName(CLASSNAME);
        addKeyDownHandler(enterDownHandler);
    }

    public TextAreaElement getTextAreaElement() {
        return super.getElement().cast();
    }

    public void setRows(int rows) {
        getTextAreaElement().setRows(rows);
    }

    public void setWordWrap(boolean wordWrap) {
        if (wordWrap == this.wordWrap) {
            return;
        }
        if (wordWrap) {
            getElement().removeAttribute("wrap");
            getElement().getStyle().clearOverflow();
            getElement().getStyle().clearWhiteSpace();
        } else {
            getElement().setAttribute("wrap", "off");
            getElement().getStyle().setOverflow(Overflow.AUTO);
            getElement().getStyle().setWhiteSpace(WhiteSpace.PRE);
        }
        if (BrowserInfo.get().isOpera()
                || (BrowserInfo.get().isWebkit() && wordWrap)) {
            // Opera fails to dynamically update the wrap attribute so we detach
            // and reattach the whole TextArea.
            // Webkit fails to properly reflow the text when enabling wrapping,
            // same workaround
            WidgetUtil.detachAttach(getElement());
        }
        this.wordWrap = wordWrap;
    }

    @Override
    public void modifyDragImage(Element element) {
        // Fix for #13557 - drag image doesn't show original text area text.
        // It happens because "value" property is not copied into the cloned
        // element
        String value = getElement().getPropertyString("value");
        if (value != null) {
            element.setPropertyString("value", value);
        }
    }
}
