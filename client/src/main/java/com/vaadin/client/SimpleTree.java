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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vaadin Ltd
 * 
 * @deprecated as of 7.1. This class was mainly used by the old debug console
 *             but is retained for now for backwards compatibility.
 */
@Deprecated
public class SimpleTree extends ComplexPanel implements HasDoubleClickHandlers {
    private Element children = Document.get().createDivElement();
    private SpanElement handle = Document.get().createSpanElement();
    private SpanElement text = Document.get().createSpanElement();

    private HandlerManager textDoubleClickHandlerManager;

    public SimpleTree() {
        setElement(Document.get().createDivElement());
        Style style = getElement().getStyle();
        style.setProperty("whiteSpace", "nowrap");
        style.setPadding(3, Unit.PX);
        style.setPaddingLeft(12, Unit.PX);
        // handle styling
        style = handle.getStyle();
        style.setDisplay(Display.NONE);
        style.setTextAlign(TextAlign.CENTER);
        style.setWidth(0.5, Unit.EM);
        style.setHeight(0.5, Unit.EM);
        style.setCursor(Cursor.POINTER);
        style.setBackgroundColor("gray");
        style.setColor("white");
        style.setPadding(4, Unit.PX);
        style.setMarginRight(3, Unit.PX);
        style.setLineHeight(0.5, Unit.EM);
        handle.setInnerHTML("+");
        getElement().appendChild(handle);
        getElement().appendChild(text);
        // children styling
        style = children.getStyle();
        style.setPaddingLeft(1.5, Unit.EM);
        style.setDisplay(Display.NONE);

        getElement().appendChild(children);
        addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeEvent().getEventTarget().cast() == handle) {
                    if (children.getStyle().getDisplay().intern() == Display.NONE
                            .getCssName()) {
                        open(event.getNativeEvent().getAltKey());
                    } else {
                        close();
                    }

                } else if (event.getNativeEvent().getEventTarget().cast() == text) {
                    select(event);
                }
            }
        }, ClickEvent.getType());
    }

    protected void select(ClickEvent event) {

    }

    public void close() {
        children.getStyle().setDisplay(Display.NONE);
        handle.setInnerHTML("+");
    }

    public void open(boolean recursive) {
        handle.setInnerHTML("-");
        children.getStyle().setDisplay(Display.BLOCK);
        if (recursive) {
            for (Widget w : getChildren()) {
                if (w instanceof SimpleTree) {
                    SimpleTree str = (SimpleTree) w;
                    str.open(true);
                }
            }
        }
    }

    public boolean isOpen() {
        return "-".equals(handle.getInnerHTML());
    }

    public String getCaption() {
        return text.getInnerText();
    }

    public SimpleTree(String caption) {
        this();
        setText(caption);
    }

    public void setText(String text) {
        this.text.setInnerText(text);
    }

    public void addItem(String text) {
        Label label = new Label(text);
        add(label, children);
    }

    @Override
    public void add(Widget child) {
        add(child, children);
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated As of 7.2, call and override {@link #add(Widget, Element)}
     *             instead.
     */
    @Override
    @Deprecated
    protected void add(Widget child,
            com.google.gwt.user.client.Element container) {
        super.add(child, container);
        handle.getStyle().setDisplay(Display.INLINE_BLOCK);
        getElement().getStyle().setPaddingLeft(3, Unit.PX);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 7.2
     */
    @Override
    protected void add(Widget child, Element container) {
        add(child, DOM.asOld(container));
    }

    /**
     * {@inheritDoc} Events are not fired when double clicking child widgets.
     */
    @Override
    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        if (textDoubleClickHandlerManager == null) {
            textDoubleClickHandlerManager = new HandlerManager(this);
            addDomHandler(new DoubleClickHandler() {
                @Override
                public void onDoubleClick(DoubleClickEvent event) {
                    if (event.getNativeEvent().getEventTarget().cast() == text) {
                        textDoubleClickHandlerManager.fireEvent(event);
                    }
                }
            }, DoubleClickEvent.getType());
        }
        return textDoubleClickHandlerManager.addHandler(
                DoubleClickEvent.getType(), handler);
    }

}
