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

package com.vaadin.client.tokka.ui;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.WidgetUtil;

/**
 * This class represents a multiline textfield (textarea).
 * 
 * @author Vaadin Ltd.
 * 
 */
public class VTextArea extends VTextField {

    public static final String CLASSNAME = "v-textarea";

    private boolean wordWrap = true;

    public VTextArea() {
        super(DOM.createTextArea());
        setStyleName(CLASSNAME);
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
}
