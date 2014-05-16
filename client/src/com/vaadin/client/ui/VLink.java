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

package com.vaadin.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.Util;
import com.vaadin.shared.ui.BorderStyle;

public class VLink extends HTML implements ClickHandler {

    public static final String CLASSNAME = "v-link";

    @Deprecated
    protected static final BorderStyle BORDER_STYLE_DEFAULT = BorderStyle.DEFAULT;

    @Deprecated
    protected static final BorderStyle BORDER_STYLE_MINIMAL = BorderStyle.MINIMAL;

    @Deprecated
    protected static final BorderStyle BORDER_STYLE_NONE = BorderStyle.NONE;

    /** For internal use only. May be removed or replaced in the future. */
    public String src;

    /** For internal use only. May be removed or replaced in the future. */
    public String target;

    /** For internal use only. May be removed or replaced in the future. */
    public BorderStyle borderStyle = BorderStyle.DEFAULT;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean enabled;

    /** For internal use only. May be removed or replaced in the future. */
    public int targetWidth;

    /** For internal use only. May be removed or replaced in the future. */
    public int targetHeight;

    /** For internal use only. May be removed or replaced in the future. */
    public Element errorIndicatorElement;

    /** For internal use only. May be removed or replaced in the future. */
    public final Element anchor = DOM.createAnchor();

    /** For internal use only. May be removed or replaced in the future. */
    public final Element captionElement = DOM.createSpan();

    /** For internal use only. May be removed or replaced in the future. */
    public Icon icon;

    public VLink() {
        super();
        getElement().appendChild(anchor);
        anchor.appendChild(captionElement);
        addClickHandler(this);
        setStyleName(CLASSNAME);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (enabled) {
            if (target == null) {
                target = "_self";
            }
            String features;
            switch (borderStyle) {
            case NONE:
                features = "menubar=no,location=no,status=no";
                break;
            case MINIMAL:
                features = "menubar=yes,location=no,status=no";
                break;
            default:
                features = "";
                break;
            }

            if (targetWidth > 0) {
                features += (features.length() > 0 ? "," : "") + "width="
                        + targetWidth;
            }
            if (targetHeight > 0) {
                features += (features.length() > 0 ? "," : "") + "height="
                        + targetHeight;
            }

            if (features.length() > 0) {
                // if 'special features' are set, use window.open(), unless
                // a modifier key is held (ctrl to open in new tab etc)
                Event e = DOM.eventGetCurrentEvent();
                if (!e.getCtrlKey() && !e.getAltKey() && !e.getShiftKey()
                        && !e.getMetaKey()) {
                    Window.open(src, target, features);
                    e.preventDefault();
                }
            }
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        final Element target = DOM.eventGetTarget(event);
        if (event.getTypeInt() == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
        }
        if (target == captionElement || target == anchor
                || (icon != null && target == icon.getElement())) {
            super.onBrowserEvent(event);
        }
        if (!enabled) {
            event.preventDefault();
        }

    }

}
