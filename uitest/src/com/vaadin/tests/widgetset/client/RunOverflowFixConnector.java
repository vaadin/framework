package com.vaadin.tests.widgetset.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.VVerticalLayout;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.orderedlayout.VerticalLayoutState;
import com.vaadin.tests.components.customlayout.OverflowAutoFix;

@SuppressWarnings("deprecation")
@Connect(OverflowAutoFix.RunOverflowFix.class)
public class RunOverflowFixConnector extends
        AbstractComponentContainerConnector {

    private static final String CONTENT1 = "Overflow:<br>Fix1 (scroll): Both scrollbars should be shown<br>Fix2 (visible): no scrollbars should be shown";
    private static final String CONTENT2 = "OverflowX:<br>Fix1 (hidden): Horizontal scrollbar should be hidden, vertical shown<br>Fix2 (scroll): Both scrollbars should be shown";
    private static final String CONTENT3 = "OverflowY:<br>Fix1 (hidden): Vertical scrollbar should be hidden, horizontal shown<br>Fix2 (auto): Both scrollbars should be shown";
    private static final String BACKGROUND = "#ddd";

    @Override
    public void init() {
        super.init();

        final Panel overflow = createScrollPanel(CONTENT1);
        overflow.addStyleName("first-scrollbar");
        final Panel overflowX = createScrollPanel(CONTENT2);
        overflowX.addStyleName("second-scrollbar");
        final Panel overflowY = createScrollPanel(CONTENT3);
        overflowY.addStyleName("third-scrollbar");

        Button runFix = new Button("Click to runWebkitOverflowAutoFix",
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        overflow.getElement().getStyle()
                                .setOverflow(Style.Overflow.SCROLL);
                        WidgetUtil.runWebkitOverflowAutoFix(overflow
                                .getElement());

                        overflowX.getElement().getStyle()
                                .setOverflowX(Style.Overflow.HIDDEN);
                        WidgetUtil.runWebkitOverflowAutoFix(overflowX
                                .getElement());

                        overflowY.getElement().getStyle()
                                .setOverflowY(Style.Overflow.HIDDEN);
                        WidgetUtil.runWebkitOverflowAutoFix(overflowY
                                .getElement());
                    }
                });
        runFix.addStyleName("run-button-one");
        getWidget().add(runFix);

        Button runFix2 = new Button("Click to runWebkitOverflowAutoFix 2",
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        overflow.getElement().getStyle()
                                .setOverflow(Style.Overflow.VISIBLE);
                        WidgetUtil.runWebkitOverflowAutoFix(overflow
                                .getElement());

                        overflowX.getElement().getStyle()
                                .setOverflowX(Style.Overflow.SCROLL);
                        WidgetUtil.runWebkitOverflowAutoFix(overflowX
                                .getElement());

                        overflowY.getElement().getStyle()
                                .setOverflowY(Style.Overflow.AUTO);
                        WidgetUtil.runWebkitOverflowAutoFix(overflowY
                                .getElement());
                    }
                });
        runFix2.addStyleName("run-button-two");
        getWidget().add(runFix2);

        addSpacer(10);
        getWidget().add(overflow);
        addSpacer(60);
        getWidget().add(overflowX);
        addSpacer(60);
        getWidget().add(overflowY);
    }

    private void addSpacer(double height) {
        Element spacer = DOM.createDiv();
        spacer.getStyle().setHeight(height, Unit.PX);
        spacer.getStyle().setWidth(10, Unit.PX);
        getWidget().getElement().appendChild(spacer);
    }

    private Panel createScrollPanel(String info) {
        FlowPanel outer = new FlowPanel();
        outer.setPixelSize(300, 200);
        getWidget().add(outer);

        HTML inner = new HTML(info);
        inner.setPixelSize(350, 250);
        inner.getElement().getStyle().setBackgroundColor(BACKGROUND);
        // for some reason theme sets size to 0
        inner.getElement().getStyle().setFontSize(12, Style.Unit.PX);
        outer.add(inner);

        return outer;
    }

    @Override
    public VVerticalLayout getWidget() {
        return (VVerticalLayout) super.getWidget();
    }

    @Override
    public VerticalLayoutState getState() {
        return (VerticalLayoutState) super.getState();
    }

    @Override
    public void updateCaption(ComponentConnector connector) {
    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    }
}