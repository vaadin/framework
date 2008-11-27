package com.itmill.toolkit.tests;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;
import com.itmill.toolkit.ui.Layout.AlignmentHandler;

public class TestOrderedLayout extends Application {

    String valignName[] = new String[] { "top", "middle", "bottom" };
    int valign[] = new int[] { OrderedLayout.ALIGNMENT_TOP,
            OrderedLayout.ALIGNMENT_VERTICAL_CENTER,
            OrderedLayout.ALIGNMENT_BOTTOM };

    Set<OrderedLayout> layouts = new HashSet<OrderedLayout>();
    private OrderedLayout layoutContainer;
    private int suffix = 0;

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        // GridLayout layout = new OrderedLayout(1, 10);
        // w.setLayout(layout);
        w.getLayout().addComponent(new Button("Swap", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                swapLayouts();
            }

        }));

        layoutContainer = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
        createUI(layoutContainer);
        w.getLayout().addComponent(layoutContainer);

        // swapLayouts();
    }

    public void swapLayouts() {
        OrderedLayout mainLayout = layoutContainer;

        int mainOrient = 1 - mainLayout.getOrientation();
        mainLayout.setOrientation(mainOrient);
        for (OrderedLayout ol : layouts) {
            ol.setOrientation(1 - mainOrient);
            float h = ol.getHeight();
            int hUnit = ol.getHeightUnits();
            float w = ol.getWidth();
            int wUnit = ol.getWidthUnits();
            ol.setWidth(h, hUnit);
            ol.setHeight(w, wUnit);

        }

    }

    private void createUI(Layout layout) {
        layout
                .addComponent(wrapLayout(layout_field_100pct_button_field(new OrderedLayout(
                        OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout.addComponent(wrapLayout(layout_overfilled(new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout
                .addComponent(wrapLayout(layout_overfilled_dynamic_height(new OrderedLayout(
                        OrderedLayout.ORIENTATION_HORIZONTAL))));
        if (true) {
            return;
        }
        layout
                .addComponent(wrapLayout(layout_symmetric_fields(new OrderedLayout(
                        OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout.addComponent(wrapLayout(layout_leftAndRight(new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout.addComponent(wrapLayout(layout_fixed_filled(new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout.addComponent(wrapLayout(layout_dynamic(new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout.addComponent(wrapLayout(layout_labels(new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout.addComponent(wrapLayout(layout_captions(new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout
                .addComponent(wrapLayout(layout_captions_fixed_size(new OrderedLayout(
                        OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout
                .addComponent(wrapLayout(layout_captions_fixed_size_and_relative_size(new OrderedLayout(
                        OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout
                .addComponent(wrapLayout(layout_captions_fixed_size_and_fixed_size(new OrderedLayout(
                        OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout
                .addComponent(wrapLayout(layout_add_remove_components(new OrderedLayout(
                        OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout.addComponent(wrapLayout(layout_pctFilled(new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout.addComponent(wrapLayout(layout_pctFilled(new OrderedLayout(
                OrderedLayout.ORIENTATION_VERTICAL))));
        layout.addComponent(wrapLayout(layout_underFilled(new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL))));
        layout.addComponent(wrapLayout(layout_basic_test(new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL))));
    }

    private Layout wrapLayout(Layout ol) {
        Panel p = new Panel(ol);
        p.setSizeUndefined();
        p.setCaption(ol.getCaption());
        ol.setCaption(null);

        OrderedLayout l = new OrderedLayout();
        l.setSizeUndefined();
        l.addComponent(p);
        // p.setWidth("600px");

        if (ol instanceof OrderedLayout) {
            layouts.add((OrderedLayout) ol);
        }
        return l;
    }

    /* LAYOUTS */

    private Layout layout1() {
        OrderedLayout ol = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        ol.setHeight("200px");
        ol.setWidth("");
        ol.setCaption("Fixed height (200px) and dynamic width");

        TextField tf = new TextField("100px high TextField, valign: bottom");
        tf.setHeight("100px");
        tf.setWidth("");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        Select s = new Select("100% high select");
        s.setMultiSelect(true);
        s.setHeight("100%");
        s.setWidth("");
        ol.addComponent(s);

        s = new Select("200 px high select");
        s.setMultiSelect(true);
        s.setHeight("200px");
        s.setWidth("");
        ol.addComponent(s);

        // tf = new TextField("100% high TextField, right/bottom");
        // tf.setHeight("100%");
        // tf.setWidth("");
        // ol.addComponent(tf);
        // ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
        // OrderedLayout.ALIGNMENT_BOTTOM);

        // tf = new TextField("100% high, 200px wide TextField");
        // tf.setHeight("100%");
        // tf.setWidth("200px");
        // ol.addComponent(tf);

        return ol;

    }

    private Layout layout2() {
        OrderedLayout ol = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        ol.setHeight("70px");
        ol.setWidth("");
        ol.setCaption("Fixed height (50px) and dynamic width");

        TextField tf = new TextField(
                "100px high TextField, valign: bottom, should be partly outside");
        tf.setHeight("100px");
        tf.setWidth("");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        tf = new TextField(
                "100% high, 50px wide TextField, valign: bottom, should fill full height");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        Label l = new Label(
                "100% high, 50px wide Label, valign: bottom, does not fill full height, only needed space");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(l);
        ol.setComponentAlignment(l, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        Select s = new Select("100% high select, should fit into layout");
        s.setMultiSelect(true);
        s.setHeight("100%");
        s.setWidth("");
        for (int i = 0; i < 10; i++) {
            s.addItem(new Object());
        }

        ol.addComponent(s);

        s = new Select("200 px high select, should be partly outside");
        s.setMultiSelect(true);
        s.setHeight("200px");
        s.setWidth("");
        ol.addComponent(s);

        return ol;
    }

    private Layout layout3() {
        OrderedLayout ol = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        ol.setHeight("");
        ol.setWidth("500px");
        ol.setCaption("Fixed width (500px) and dynamic height");
        TextField tf;

        tf = new TextField("100px high TextField, valign: bottom");
        tf.setHeight("100px");
        tf.setWidth("100%");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        tf = new TextField("100px high TextField, valign: top");
        tf.setHeight("100px");
        tf.setWidth("100%");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_TOP);

        tf = new TextField("100% high, 50px wide TextField, valign: bottom");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        Label l = new Label(
                "100% high, 50px wide Label, valign: bottom, does not fill full height, only needed space");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(l);
        ol.setComponentAlignment(l, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        Select s = new Select("100% high select, should fit into layout");
        s.setMultiSelect(true);
        s.setHeight("100%");
        s.setWidth("100%");
        for (int i = 0; i < 10; i++) {
            s.addItem(new Object());
        }

        ol.addComponent(s);

        s = new Select("200 px high select, should make the layout 200px high");
        s.setMultiSelect(true);
        s.setHeight("200px");
        s.setWidth("100%");
        ol.addComponent(s);

        return ol;
    }

    private Layout layout3New() {
        OrderedLayout ol = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        ol.setHeight("300px");
        // ol.setWidth("500px");
        ol.setWidth("");
        ol.setCaption("Dynamic width and fixed height(300px)");
        TextField tf;

        tf = new TextField("100px high TextField, valign: bottom");
        tf.setHeight("100px");
        tf.setWidth("100%");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        tf = new TextField("100px high TextField, valign: top");
        tf.setHeight("100px");
        tf.setWidth("100%");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_TOP);

        tf = new TextField("100% high, 50px wide TextField, valign: bottom");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        Label l = new Label(
                "100% high, 50px wide Label, valign: bottom, does not fill full height, only needed space");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(l);
        ol.setComponentAlignment(l, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        Select s = new Select("100% high select, should fit into layout");
        s.setMultiSelect(true);
        s.setHeight("100%");
        s.setWidth("100%");
        for (int i = 0; i < 10; i++) {
            s.addItem(new Object());
        }

        ol.addComponent(s);

        s = new Select("200 px high select, should make the layout 200px high");
        s.setMultiSelect(true);
        s.setHeight("200px");
        s.setWidth("100%");
        ol.addComponent(s);

        return ol;
    }

    private Layout layout4(OrderedLayout ol) {
        // ol.setHeight("300px");
        // ol.setWidth("500px");
        ol.setMargin(true);
        ol.setSpacing(true);
        ol.setWidth("");
        ol.setCaption("Dynamic width and dynamic height");
        TextField tf;

        tf = new TextField("100% high TextField");
        tf.setCaption(null);
        tf.setRequired(true);
        tf.setValue("100% high Field");
        tf.setHeight("100%");
        tf.setWidth("100px");
        tf.setRows(2);
        ol.addComponent(tf);

        tf = new TextField("100% high TextField");
        tf.setCaption("100% high TextField");
        tf.setRequired(true);
        tf.setValue("100% high Field");
        tf.setHeight("100%");
        tf.setWidth("100px");
        tf.setRows(2);
        ol.addComponent(tf);

        for (int i = 1; i < 4; i++) {
            int w = i * 100;
            tf = new TextField("Field " + i);
            tf.setRows(2);
            tf.setValue(w + "px high, " + w + "px wide TextField, valign: "
                    + valignName[i % 3]);
            tf.setWidth(w + "px");
            tf.setHeight(w + "px");
            ol.addComponent(tf);
            ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                    valign[i % 3]);

        }

        tf = new TextField("100% high TextField");
        tf.setValue("100% high 100px wide");
        tf.setRows(2);
        tf.setHeight("100%");
        tf.setWidth("100px");
        ol.addComponent(tf);
        return ol;
    }

    private Layout layout_field_100pct_button_field(OrderedLayout ol) {
        ol.setHeight("500px");
        ol.setWidth("916px");
        ol.setMargin(false);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight()
                + "px) / layout_field_100pct_button_field");
        TextField tf;

        tf = new TextField("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_TOP);
        ol.addComponent(tf);

        Button b;
        b = new Button("This is a 100%x50% valign middle button");
        b.setSizeFull();
        b.setHeight("50%");
        ol.addComponent(b);
        ol.setExpandRatio(b, 1.0f);
        ol.setComponentAlignment(b, AlignmentHandler.ALIGNMENT_RIGHT,
                AlignmentHandler.ALIGNMENT_VERTICAL_CENTER);

        tf = new TextField("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);
        ol.addComponent(tf);
        return ol;
    }

    private Layout layout_basic_test(OrderedLayout ol) {
        ol.setHeight("700px");
        ol.setWidth("900px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight()
                + "px) / layout_basic_test");
        TextField tf;

        tf = new TextField("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_TOP);
        ol.addComponent(tf);

        Button b;
        // b = new Button("This is a 100%x50% valign middle button");
        // b.setSizeFull();
        // b.setHeight("50%");
        // ol.addComponent(b, 1.0f);
        // ol.setComponentAlignment(b, AlignmentHandler.ALIGNMENT_RIGHT,
        // AlignmentHandler.ALIGNMENT_VERTICAL_CENTER);

        tf = new TextField("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);
        ol.addComponent(tf);
        return ol;
    }

    private Layout layout_symmetric_fields(OrderedLayout ol) {
        ol.setHeight("900px");
        ol.setWidth("900px");
        ol.setMargin(false);
        ol.setSpacing(false);

        // ol.setWidth("");
        ol.setCaption("Fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight()
                + "px) / layout_symmetric_fields");
        TextField tf;

        tf = new TextField("300px x 300px Field");
        tf.setValue("300x300 field");
        tf.setRows(2);
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_TOP);
        ol.addComponent(tf);

        tf = new TextField("300px x 300px Field");
        tf.setValue("300x300 field");
        tf.setRows(2);
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER,
                OrderedLayout.ALIGNMENT_VERTICAL_CENTER);
        ol.addComponent(tf);

        tf = new TextField("300px x 300px Field");
        tf.setValue("300x300 field");
        tf.setRows(2);
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);
        ol.addComponent(tf);

        return ol;
    }

    private Layout layout_leftAndRight(OrderedLayout ol) {
        ol.setHeight("700px");
        ol.setWidth("700px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight()
                + "px) / layout_leftAndRight");
        TextField tf;

        // tf = new TextField("100%x100% Field");
        // tf.setCaption(null);
        // tf.setValue("100% x 100% TextField");
        // tf.setSizeFull();
        // tf.setRequired(true);
        // // tf.setComponentError(new UserError("It's broken!"));
        //
        // // tf.setHeight("100%");
        // // tf.setWidth("100px");
        // tf.setRows(2);
        // ol.addComponent(tf);
        //
        // for (int i = 1; i < 5; i++) {
        // int w = i * 100;
        // tf = new TextField("Caption field " + i);
        // tf.setRows(2);
        // tf.setValue(w + "px high, " + w + "px wide TextField, valign: "
        // + valignName[i % 3]);
        // tf.setWidth(w + "px");
        // tf.setHeight(w + "px");
        // ol.addComponent(tf);
        // ol.setComponentAlignment(tf,
        // OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER, valign[i % 3]);
        // }
        //
        // tf.setValue(tf.getValue().toString() + " (100% wide)");
        // tf.setWidth("100%");

        // tf = new TextField("100%x70px Field");
        // tf.setCaption(null);
        // tf.setRequired(true);
        // // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        // tf.setComponentError(new UserError("abc"));
        // tf.setValue("100% high 70px wide TextField");
        // tf.setRows(2);
        // // tf.setSizeFull();
        // tf.setHeight("100%");
        // tf.setWidth("70px");
        // ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
        // OrderedLayout.ALIGNMENT_TOP);
        // ol.addComponent(tf);

        tf = new TextField("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_TOP);
        ol.addComponent(tf);

        tf = new TextField("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);
        ol.addComponent(tf);
        return ol;
    }

    private Layout layout_fixed_filled(OrderedLayout ol) {
        ol.setHeight("700px");
        ol.setWidth("700px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Filled with fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight() + "px)");
        TextField tf;

        tf = new TextField("60%x100% Field");
        tf.setCaption("This one has a caption");
        tf.setValue("60% x 100% TextField");
        tf.setWidth("100%");
        tf.setHeight("100%");
        tf.setRequired(true);
        // tf.setComponentError(new UserError("It's broken!"));

        // tf.setHeight("100%");
        // tf.setWidth("100px");
        tf.setRows(2);
        ol.addComponent(tf);
        ol.setExpandRatio(tf, 1f);
        //

        tf = new TextField("60%x60% Field");
        tf.setCaption(null);
        tf.setValue("60% x 60% TextField");
        tf.setWidth("100%");
        tf.setHeight("60%");
        tf.setRequired(true);
        ol.addComponent(tf);
        ol.setExpandRatio(tf, 1f);
        ol.setComponentAlignment(tf, AlignmentHandler.ALIGNMENT_LEFT,
                AlignmentHandler.ALIGNMENT_VERTICAL_CENTER);
        // tf.setComponentError(new UserError("It's broken!"));

        // tf.setHeight("100%");
        // tf.setWidth("100px");
        tf.setRows(2);
        //
        // for (int i = 1; i < 5; i++) {
        // int w = i * 100;
        // tf = new TextField("Caption field " + i);
        // tf.setRows(2);
        // tf.setValue(w + "px high, " + w + "px wide TextField, valign: "
        // + valignName[i % 3]);
        // tf.setWidth(w + "px");
        // tf.setHeight(w + "px");
        // ol.addComponent(tf);
        // ol.setComponentAlignment(tf,
        // OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER, valign[i % 3]);
        // }
        //
        // tf.setValue(tf.getValue().toString() + " (100% wide)");
        // tf.setWidth("100%");

        // tf = new TextField("100%x70px Field");
        // tf.setCaption(null);
        // tf.setRequired(true);
        // // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        // tf.setComponentError(new UserError("abc"));
        // tf.setValue("100% high 70px wide TextField");
        // tf.setRows(2);
        // // tf.setSizeFull();
        // tf.setHeight("100%");
        // tf.setWidth("70px");
        // ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
        // OrderedLayout.ALIGNMENT_TOP);
        // ol.addComponent(tf);

        tf = new TextField("200px x 200px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("200x200 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("200px");
        tf.setWidth("200px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_TOP);
        ol.addComponent(tf);

        tf = new TextField("200px x 200px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("200x200 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("200px");
        tf.setWidth("200px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);
        ol.addComponent(tf);
        return ol;
    }

    private Layout layout_overfilled(OrderedLayout ol) {
        ol.setHeight("300px");
        ol.setWidth("700px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("OverFilled with fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight() + "px)");
        TextField tf;

        for (int i = 0; i < 5; i++) {
            tf = new TextField("200x200px Field");
            tf.setCaption("This one has a caption");
            tf.setValue("200x200 TextField");
            tf.setWidth("200px");
            tf.setHeight("200px");
            tf.setRequired(true);
            // tf.setComponentError(new UserError("It's broken!"));

            // tf.setHeight("100%");
            // tf.setWidth("100px");
            tf.setRows(2);
            ol.addComponent(tf);
        }

        return ol;
    }

    private Layout layout_overfilled_dynamic_height(OrderedLayout ol) {
        ol.setHeight(null);
        ol.setWidth("700px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("OverFilled with fixed width (" + ol.getWidth()
                + "px) and dynamic height");
        TextField tf;

        for (int i = 0; i < 10; i++) {
            tf = new TextField("200x200px Field");
            tf.setCaption("This one has a caption");
            tf.setWidth("200px");
            tf.setHeight(((i + 1) * 50) + "px");
            tf.setValue(tf.getWidth() + "x" + tf.getHeight() + " TextField");
            tf.setRequired(true);
            // tf.setComponentError(new UserError("It's broken!"));

            // tf.setHeight("100%");
            // tf.setWidth("100px");
            tf.setRows(2);
            ol.addComponent(tf);
        }

        return ol;
    }

    // private Layout layout_add_components(OrderedLayout ol) {
    // ol.setHeight("600px");
    // ol.setWidth("600px");
    // ol.setMargin(true);
    // ol.setSpacing(true);
    //
    // // ol.setWidth("");
    // ol.setCaption("Fixed width (" + ol.getWidth()
    // + "px) and fixed height (" + ol.getHeight() + "px)");
    //
    // for (int i = 0; i < 3; i++) {
    // Button b = createAddButton(ol);
    // ol.addComponent(b);
    // }
    //
    // return ol;
    //
    // }

    private Layout layout_add_remove_components(OrderedLayout ol) {
        ol.setHeight("600px");
        ol.setWidth("600px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight()
                + "px) / layout_add_remove_components");

        for (int i = 0; i < 2; i++) {
            OrderedLayout inner = createAddRemove(ol, "", "");
            ol.addComponent(inner);
            ol.setComponentAlignment(inner, OrderedLayout.ALIGNMENT_RIGHT,
                    OrderedLayout.ALIGNMENT_BOTTOM);
        }

        return ol;

    }

    private Layout layout_dynamic(OrderedLayout ol) {
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Dynamic width, dynamic height");

        for (int i = 0; i < 3; i++) {
            Button b = new Button("Button " + i);
            if (i == 2) {
                b.setHeight("200px");
            } else {
                b.setHeight("100%");
            }
            ol.addComponent(b);
        }

        return ol;

    }

    private Layout layout_captions(OrderedLayout ol) {
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Caption test with dynamic width");

        TextField tf;
        tf = new TextField("Short caption");
        ol.addComponent(tf);

        tf = new TextField(
                "A very long caption which is probably much longer than the field");
        ol.addComponent(tf);

        tf = new TextField(
                "A very long caption which is probably much longer than the field and includes indicators");
        tf.setRequired(true);
        tf.setComponentError(new UserError("abc123"));
        ol.addComponent(tf);

        // for (int i = 0; i < 3; i++) {
        // Button b = new Button("Button " + i);
        // if (i == 2) {
        // b.setHeight("200px");
        // } else {
        // b.setHeight("100%");
        // }
        // ol.addComponent(b);
        // }

        return ol;

    }

    private Layout layout_captions_fixed_size(OrderedLayout ol) {
        ol.setWidth("700px");
        ol.setHeight("250px");

        ol.setMargin(false);
        ol.setSpacing(false);

        // ol.setWidth("");
        ol.setCaption("Caption test with fixed size");

        TextField tf;
        tf = new TextField("Short caption");
        tf.setValue("Undefined width");
        tf.setComponentError(new UserError("123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        tf = new TextField(
                "A long caption which is probably much longer than the field");
        tf.setValue("Undefined width");
        tf.setRequired(true);
        tf.setComponentError(new UserError("123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        tf = new TextField(
                "A very long caption which is probably much longer than the field and includes indicators");
        tf.setValue("Undefined width");
        tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setRequired(true);
        tf.setComponentError(new UserError("abc123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        // for (int i = 0; i < 3; i++) {
        // Button b = new Button("Button " + i);
        // if (i == 2) {
        // b.setHeight("200px");
        // } else {
        // b.setHeight("100%");
        // }
        // ol.addComponent(b);
        // }

        return ol;

    }

    private Layout layout_captions_fixed_size_and_relative_size(OrderedLayout ol) {
        ol.setWidth("700px");
        ol.setHeight("250px");

        ol.setMargin(false);
        ol.setSpacing(false);

        // ol.setWidth("");
        ol.setCaption("Caption test with fixed width (700x250)");

        TextField tf;
        tf = new TextField("Short caption");
        tf.setSizeFull();
        tf.setValue("100% wide field, ratio 1");

        tf.setComponentError(new UserError("123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);
        ol.setExpandRatio(tf, 1);

        tf = new TextField(
                "A long caption which is probably much longer than the field");
        tf.setValue("100% wide field, ratio 2");
        tf.setSizeFull();
        tf.setRequired(true);
        tf.setComponentError(new UserError("123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);
        ol.setExpandRatio(tf, 2);

        tf = new TextField(
                "A very long caption which is probably much longer than the field and includes indicators");
        tf.setValue("100% wide field, ratio 3");
        tf.setSizeFull();
        tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setRequired(true);
        tf.setComponentError(new UserError("abc123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);
        ol.setExpandRatio(tf, 3);

        // for (int i = 0; i < 3; i++) {
        // Button b = new Button("Button " + i);
        // if (i == 2) {
        // b.setHeight("200px");
        // } else {
        // b.setHeight("100%");
        // }
        // ol.addComponent(b);
        // }

        return ol;

    }

    private Layout layout_captions_fixed_size_and_fixed_size(OrderedLayout ol) {
        ol.setWidth("700px");
        ol.setHeight("250px");

        ol.setMargin(false);
        ol.setSpacing(false);

        // ol.setWidth("");
        ol.setCaption("Caption test with fixed width");

        TextField tf;
        tf = new TextField("Short caption");
        tf.setValue("250px wide field");
        tf.setWidth("250px");
        tf.setComponentError(new UserError("123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        tf = new TextField(
                "A long caption which is probably much longer than the field");
        tf.setWidth("250px");
        tf.setValue("250px wide field");
        tf.setRequired(true);
        tf.setComponentError(new UserError("123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        tf = new TextField(
                "A very long caption which is probably much longer than the field and includes indicators");
        tf.setValue("200px wide field");
        tf.setWidth("200px");
        tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setRequired(true);
        tf.setComponentError(new UserError("abc123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);

        // for (int i = 0; i < 3; i++) {
        // Button b = new Button("Button " + i);
        // if (i == 2) {
        // b.setHeight("200px");
        // } else {
        // b.setHeight("100%");
        // }
        // ol.addComponent(b);
        // }

        return ol;

    }

    private Layout layout_labels(OrderedLayout ol) {
        // ol.setWidth("700px");
        // ol.setHeight("200px");

        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Caption test with fixed width");

        Label l;
        l = new Label(
                "This is a long text and should remain on one line as there is nothing forcing line breaks");
        ol.addComponent(l);
        // ol.setComponentAlignment(l, OrderedLayout.ALIGNMENT_RIGHT,
        // OrderedLayout.ALIGNMENT_BOTTOM);

        l = new Label("WTF OMG LOL");
        ol.addComponent(l);
        // ol.setComponentAlignment(l, OrderedLayout.ALIGNMENT_RIGHT,
        // OrderedLayout.ALIGNMENT_BOTTOM);

        return ol;

    }

    private OrderedLayout createAddRemove(OrderedLayout ol, String width,
            String buttonSuffix) {
        Button b = createAddButton(ol);
        Button wb = createWideAddButton(ol);
        Button r = createRemoveButton(ol, buttonSuffix);
        OrderedLayout inner = new OrderedLayout(
                OrderedLayout.ORIENTATION_VERTICAL);
        inner.setCaption("Width: " + width);
        inner.setWidth(width);

        inner.addComponent(b);
        inner.addComponent(wb);
        inner.addComponent(r);

        // inner.setHeight("132px");
        return inner;
    }

    private Button createAddButton(OrderedLayout ol) {
        Button b = new Button("Add before", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                addBefore((OrderedLayout) event.getButton().getData(), event
                        .getButton().getParent(), "");
            }

        });
        b.setData(ol);

        return b;
    }

    private Button createWideAddButton(OrderedLayout ol) {
        Button b = new Button("Add 100% before", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                addBefore((OrderedLayout) event.getButton().getData(), event
                        .getButton().getParent(), "100%");
            }

        });
        b.setData(ol);

        return b;
    }

    private Button createRemoveButton(OrderedLayout ol, String suffix) {
        Button b = new Button("Remove this " + suffix, new ClickListener() {

            public void buttonClick(ClickEvent event) {
                remove((OrderedLayout) event.getButton().getData(), event
                        .getButton().getParent());
            }

        });
        b.setWidth("100%");
        b.setData(ol);

        return b;
    }

    protected void remove(OrderedLayout ol, Component c) {
        ol.removeComponent(c);

    }

    protected void addBefore(OrderedLayout ol, Component c, String width) {
        int index = 0;
        Iterator iter = ol.getComponentIterator();
        while (iter.hasNext()) {
            if (iter.next() == c) {
                break;
            }
            index++;
        }
        OrderedLayout inner = createAddRemove(ol, width, String
                .valueOf(suffix++));
        ol.addComponent(inner, index);
        if (width.contains("%")) {
            ol.setExpandRatio(inner, 1.0f);
        }

        ol.setComponentAlignment(inner, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);

    }

    private Layout layout_pctFilled(OrderedLayout ol) {
        ol.setHeight("600px");
        ol.setWidth("600px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("100 % filled with fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight() + "px)");
        TextField tf;

        tf = new TextField();
        tf.setCaption("This one has a caption");
        tf.setValue("60% expand TextField");
        tf.setWidth("100%");
        tf.setHeight("100%");
        // tf.setRequired(true);
        // tf.setComponentError(new UserError("It's broken!"));

        // tf.setHeight("100%");
        // tf.setWidth("100px");
        tf.setRows(2);
        ol.addComponent(tf);
        ol.setExpandRatio(tf, 60);

        tf = new TextField();
        tf.setValue("100px 100px TextField");
        tf.setWidth("100px");
        tf.setHeight("100px");
        tf.setRows(2);
        ol.addComponent(tf);
        ol.setComponentAlignment(tf,
                AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER,
                AlignmentHandler.ALIGNMENT_VERTICAL_CENTER);

        //

        tf = new TextField("40%x40% Field");
        // tf.setCaption(null);
        tf.setValue("40% expand (40% height) TextField");
        tf.setWidth("100%");
        tf.setHeight("40%");
        ol.addComponent(tf);
        ol.setExpandRatio(tf, 40);
        // tf.setRequired(true);
        ol.setComponentAlignment(tf, AlignmentHandler.ALIGNMENT_RIGHT,
                AlignmentHandler.ALIGNMENT_BOTTOM);

        tf.setRows(2);

        return ol;
    }

    private Layout layout_pctFilled2(OrderedLayout ol) {
        ol.setHeight("600px");
        ol.setWidth("600px");
        ol.setMargin(true);
        ol.setSpacing(false);

        // ol.setWidth("");
        ol.setCaption("100 % filled with fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight() + "px)");
        TextField tf;

        tf = new TextField();
        // tf.setCaption("This one has a caption");
        tf.setValue("80% x 20% TextField");
        tf.setWidth("80%");
        tf.setHeight("20%");
        // tf.setRequired(true);
        // tf.setComponentError(new UserError("It's broken!"));

        // tf.setHeight("100%");
        // tf.setWidth("100px");
        tf.setRows(2);
        ol.addComponent(tf);
        //

        tf = new TextField("20%x60% Field");
        tf.setCaption(null);
        tf.setValue("20% x 60% TextField");
        tf.setWidth("20%");
        tf.setHeight("60%");
        // tf.setRequired(true);
        ol.setComponentAlignment(tf, AlignmentHandler.ALIGNMENT_RIGHT,
                AlignmentHandler.ALIGNMENT_BOTTOM);

        tf.setRows(2);
        ol.addComponent(tf);

        return ol;
    }

    private Layout layout_underFilled(OrderedLayout ol) {
        ol.setHeight("700px");
        ol.setWidth("700px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Underfilled with fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight() + "px)");
        TextField tf;

        tf = new TextField("60%x100% Field");
        tf.setCaption("Short capt");
        tf.setValue("60% x 100% TextField");
        tf.setWidth("60%");
        tf.setHeight("100%");
        tf.setRequired(true);
        tf.setRows(2);

        ol.setComponentAlignment(tf,
                AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER,
                AlignmentHandler.ALIGNMENT_VERTICAL_CENTER);

        ol.addComponent(tf);

        tf = new TextField("200px x 200px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("200x200 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("200px");
        tf.setWidth("200px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_TOP);
        ol.addComponent(tf);

        tf = new TextField("200px x 200px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("200x200 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("200px");
        tf.setWidth("200px");
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_BOTTOM);
        ol.addComponent(tf);
        return ol;
    }
}