package com.vaadin.tests.widgetset.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.WidgetUtil;

public class WidgetUtilTestWidget extends Widget {

    private Element noBorderPadding;
    private Element border;
    private Element padding;
    private Element borderPadding;
    private DivElement result;
    private boolean inline;
    private DivElement root;

    public WidgetUtilTestWidget() {
        Document doc = Document.get();

        root = doc.createDivElement();
        root.addClassName("v-widget-util-test");
        setElement(root);

        addDomHandler(event -> showSizes(), ClickEvent.getType());
    }

    public void construct(boolean inline) {
        this.inline = inline;

        noBorderPadding = createElement();

        border = createElement();
        setBorder(border);

        padding = createElement();
        setPadding(padding);

        borderPadding = createElement();
        setBorder(borderPadding);
        setPadding(borderPadding);

        root.appendChild(noBorderPadding);
        root.appendChild(border);
        root.appendChild(padding);
        root.appendChild(borderPadding);

        result = Document.get().createDivElement();
        result.addClassName("result");
        result.getStyle().setWidth(500, Unit.PX);
        root.appendChild(result);
    }

    private void setBorder(Element e) {
        Style borderStyle = e.getStyle();
        borderStyle.setBorderStyle(BorderStyle.SOLID);
        borderStyle.setBorderWidth(1.8, Unit.PX);
    }

    private void setPadding(Element e) {
        Style borderStyle = e.getStyle();
        borderStyle.setPaddingLeft(2.4, Unit.PX);
        borderStyle.setPaddingRight(3.5, Unit.PX);
        borderStyle.setPaddingTop(2.4, Unit.PX);
        borderStyle.setPaddingBottom(3.5, Unit.PX);
    }

    private Element createElement() {
        Element e;
        if (inline) {
            e = Document.get().createSpanElement();
            e.getStyle().setBackgroundColor("green");
        } else {
            e = Document.get().createDivElement();
            e.getStyle().setWidth(300, Unit.PX);
            e.getStyle().setHeight(50, Unit.PX);
            e.getStyle().setBackgroundColor("blue");
        }
        e.getStyle().setMargin(3.7, Unit.PX);
        return e;
    }

    public void showSizes() {
        String sizes = "Measured required width x height<br/>";

        sizes += "<div id='noBorderPadding'>noBorderPadding: "
                + WidgetUtil.getRequiredWidthComputedStyle(noBorderPadding);
        sizes += "x"
                + WidgetUtil.getRequiredHeightComputedStyle(noBorderPadding)
                + "</div>";
        sizes += "<div id='border'>border: "
                + WidgetUtil.getRequiredWidthComputedStyle(border);
        sizes += "x" + WidgetUtil.getRequiredHeightComputedStyle(border);
        sizes += "</div>";

        sizes += "<div id='padding'>padding: "
                + WidgetUtil.getRequiredWidthComputedStyle(padding);
        sizes += "x" + WidgetUtil.getRequiredHeightComputedStyle(padding);
        sizes += "</div>";

        sizes += "<div id='borderPadding'>borderPadding: "
                + WidgetUtil.getRequiredWidthComputedStyle(borderPadding);
        sizes += "x" + WidgetUtil.getRequiredHeightComputedStyle(borderPadding)
                + "</div>";

        result.setInnerHTML(sizes);

        if (inline) {
            result.getStyle().setPaddingTop(200, Unit.PX);
        }

    }

}
