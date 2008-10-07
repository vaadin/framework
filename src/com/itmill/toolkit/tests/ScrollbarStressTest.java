package com.itmill.toolkit.tests;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Accordion;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OptionGroup;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class ScrollbarStressTest extends Application {

    final Window main = new Window("Scrollbar Stress Test");

    final Panel panel = new Panel("Panel");
    final SplitPanel splitPanel = new SplitPanel();
    final Accordion accordion = new Accordion();
    final TabSheet tabsheet = new TabSheet();
    final Window subwindow = new Window("Subwindow");

    final OptionGroup width = new OptionGroup("LO Width");
    final OptionGroup height = new OptionGroup("LO Height");

    private boolean getTable;

    @Override
    public void init() {
        setTheme("tests-tickets");
        setMainWindow(main);
        createControlWindow();
        subwindow.setWidth("400px");
        subwindow.setHeight("400px");
    }

    private void createControlWindow() {
        final OptionGroup context = new OptionGroup("Context");
        context.addItem("Main window");
        context.addItem("ExpandLayout");
        context.addItem("Subwindow");
        context.addItem("Panel");
        context.addItem("Split Panel");
        context.addItem("TabSheet");
        context.addItem("Accordion");
        context.setValue("Main window");

        final OptionGroup testComponent = new OptionGroup(
                "TestComponent 100%x100%");
        testComponent.addItem("Label");
        testComponent.addItem("Table");
        testComponent.setValue("Label");

        width.addItem("100%");
        width.addItem("50%");
        width.addItem("150%");
        width.addItem("100px");
        width.addItem("500px");
        width.setValue("100%");

        height.addItem("100%");
        height.addItem("50%");
        height.addItem("150%");
        height.addItem("100px");
        height.addItem("500px");
        height.setValue("100%");

        final Button set = new Button("Set", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                getTable = testComponent.getValue().equals("Table");

                if (context.getValue() == "Main window") {
                    drawInMainWindow();
                } else if (context.getValue() == "Subwindow") {
                    drawInSubwindow();
                } else if (context.getValue() == "Panel") {
                    drawInPanel();
                } else if (context.getValue() == "Split Panel") {
                    drawInSplitPanel();
                } else if (context.getValue() == "TabSheet") {
                    drawInTabSheet(false);
                } else if (context.getValue() == "Accordion") {
                    drawInTabSheet(true);
                } else if (context.getValue() == "ExpandLayout") {
                    drawInExpandLayout();
                }
            }
        });

        OrderedLayout ol = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        ol.addComponent(context);
        ol.addComponent(testComponent);
        ol.addComponent(width);
        ol.addComponent(height);
        ol.addComponent(set);
        ol.setSpacing(true);
        ol.setMargin(true);

        Window controller = new Window("Controller");
        controller.setLayout(ol);
        main.addWindow(controller);
    }

    protected void drawInExpandLayout() {
        main.removeAllComponents();
        main.getLayout().setSizeFull();

        OrderedLayout ol = new OrderedLayout();

        ExpandLayout el = new ExpandLayout();

        el.removeAllComponents();

        ol.setWidth((String) width.getValue());
        ol.setHeight((String) height.getValue());

        ol.addComponent(getTestComponent());

        el.addComponent(ol);

        main.getLayout().addComponent(el);
        main.removeWindow(subwindow);

    }

    protected void drawInTabSheet(boolean verticalAkaAccordion) {
        main.removeAllComponents();
        main.getLayout().setSizeFull();

        OrderedLayout ol = new OrderedLayout();
        ol.setCaption("Tab 1");
        OrderedLayout ol2 = new OrderedLayout();
        ol2.setCaption("Tab 2");

        TabSheet ts = (verticalAkaAccordion ? accordion : tabsheet);
        ts.setSizeFull();

        ts.removeAllComponents();

        ts.addComponent(ol);
        ts.addComponent(ol2);

        ol.setWidth((String) width.getValue());
        ol.setHeight((String) height.getValue());
        ol2.setWidth((String) width.getValue());
        ol2.setHeight((String) height.getValue());

        ol.addComponent(getTestComponent());

        ol2.addComponent(getTestComponent());

        main.addComponent(ts);
        main.removeWindow(subwindow);
    }

    private void drawInSplitPanel() {
        main.removeAllComponents();
        main.getLayout().setSizeFull();

        OrderedLayout ol = new OrderedLayout();
        OrderedLayout ol2 = new OrderedLayout();

        splitPanel.setFirstComponent(ol);
        splitPanel.setSecondComponent(ol2);

        ol.setWidth((String) width.getValue());
        ol.setHeight((String) height.getValue());
        ol2.setWidth((String) width.getValue());
        ol2.setHeight((String) height.getValue());

        ol.addComponent(getTestComponent());

        ol2.addComponent(getTestComponent());

        main.addComponent(splitPanel);
        main.removeWindow(subwindow);
    }

    private void drawInPanel() {
        main.removeAllComponents();
        main.getLayout().setSizeFull();

        OrderedLayout ol = new OrderedLayout();
        panel.setSizeFull();
        panel.setLayout(ol);

        ol.setWidth((String) width.getValue());
        ol.setHeight((String) height.getValue());

        ol.addComponent(getTestComponent());
        main.addComponent(panel);
        main.removeWindow(subwindow);
    }

    private void drawInSubwindow() {
        main.removeAllComponents();
        main.getLayout().setSizeFull();
        OrderedLayout ol = new OrderedLayout();
        ol.setWidth((String) width.getValue());
        ol.setHeight((String) height.getValue());

        ol.addComponent(getTestComponent());
        subwindow.setLayout(ol);
        main.addWindow(subwindow);
    }

    private void drawInMainWindow() {
        main.removeAllComponents();
        OrderedLayout ol = new OrderedLayout();
        main.setLayout(ol);
        ol.setWidth((String) width.getValue());
        ol.setHeight((String) height.getValue());

        ol.addComponent(getTestComponent());
        main.removeWindow(subwindow);
    }

    private Component getTestComponent() {
        if (getTable) {
            Table testTable = TestForTablesInitialColumnWidthLogicRendering
                    .getTestTable(4, 50);
            testTable.setSizeFull();
            return testTable;
        } else {
            Label l = new Label("Label");
            l.setStyleName("no-padding");
            l.setSizeFull();
            return l;
        }
    }
}
