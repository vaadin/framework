/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SimpleTree extends ComplexPanel {
    private Element children = Document.get().createDivElement().cast();
    private SpanElement handle = Document.get().createSpanElement();
    private SpanElement text = Document.get().createSpanElement();

    public SimpleTree() {
        setElement(Document.get().createDivElement());
        Style style = getElement().getStyle();
        style.setProperty("whiteSpace", "nowrap");
        style.setPadding(3, Unit.PX);

        style = handle.getStyle();
        style.setDisplay(Display.NONE);
        style.setProperty("textAlign", "center");
        style.setWidth(10, Unit.PX);
        style.setCursor(Cursor.POINTER);
        style.setBorderStyle(BorderStyle.SOLID);
        style.setBorderColor("grey");
        style.setBorderWidth(1, Unit.PX);
        style.setMarginRight(3, Unit.PX);
        style.setProperty("borderRadius", "4px");
        handle.setInnerHTML("+");
        getElement().appendChild(handle);
        getElement().appendChild(text);
        style = children.getStyle();
        style.setPaddingLeft(20, Unit.PX);
        style.setDisplay(Display.NONE);

        getElement().appendChild(children);
        addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.getNativeEvent().getEventTarget().cast() == handle) {
                    if (children.getStyle().getDisplay().intern() == Display.NONE
                            .getCssName()) {
                        open(event.getNativeEvent().getShiftKey());
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

    @Override
    protected void add(Widget child, Element container) {
        super.add(child, container);
        handle.getStyle().setDisplay(Display.INLINE_BLOCK);
    }

}
