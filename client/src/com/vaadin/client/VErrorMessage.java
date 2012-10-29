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

package com.vaadin.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.VOverlay;

public class VErrorMessage extends FlowPanel {
    public static final String CLASSNAME = "v-errormessage";

    private Widget owner;

    public VErrorMessage() {
        super();
        setStyleName(CLASSNAME);
    }

    /**
     * Set the owner, i.e the Widget that created this {@link VErrorMessage}.
     * The owner must be set if the {@link VErrorMessage} is created
     * 'stand-alone' (not within a {@link VOverlay}), or theming might not work
     * properly.
     * 
     * @see VOverlay#setOwner(Widget)
     * @param owner
     *            the owner (creator Widget)
     */
    public void setOwner(Widget owner) {
        this.owner = owner;
    }

    public void updateMessage(String htmlErrorMessage) {
        clear();
        if (htmlErrorMessage == null || htmlErrorMessage.length() == 0) {
            add(new HTML(" "));
        } else {
            // pre-formatted on the server as div per child
            add(new HTML(htmlErrorMessage));
        }
    }

    /**
     * Shows this error message next to given element.
     * 
     * @param indicatorElement
     */
    public void showAt(Element indicatorElement) {
        VOverlay errorContainer = (VOverlay) getParent();
        if (errorContainer == null) {
            errorContainer = new VOverlay();
            errorContainer.setWidget(this);
            errorContainer.setOwner(owner);
        }
        errorContainer.setPopupPosition(
                DOM.getAbsoluteLeft(indicatorElement)
                        + 2
                        * DOM.getElementPropertyInt(indicatorElement,
                                "offsetHeight"),
                DOM.getAbsoluteTop(indicatorElement)
                        + 2
                        * DOM.getElementPropertyInt(indicatorElement,
                                "offsetHeight"));
        errorContainer.show();

    }

    public void hide() {
        final VOverlay errorContainer = (VOverlay) getParent();
        if (errorContainer != null) {
            errorContainer.hide();
        }
    }
}
