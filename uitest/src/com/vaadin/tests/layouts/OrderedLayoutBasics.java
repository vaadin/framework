package com.vaadin.tests.layouts;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class OrderedLayoutBasics extends TestBase {

    String valignName[] = new String[] { "top", "middle", "bottom" };

    Set<AbstractOrderedLayout> layouts = new HashSet<AbstractOrderedLayout>();
    private AbstractOrderedLayout layoutContainer;
    private int suffix = 0;

    @Override
    protected String getDescription() {
        return "Various layout tests for VerticalLayout and HorizontalLayout";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    public void setup() {
        getMainWindow().getContent().setHeight(null);

        layoutContainer = new VerticalLayout();
        createUI(layoutContainer);
        addComponent(layoutContainer);
    }

    private void createUI(Layout layout) {
        layout.addComponent(wrapLayout(layout_field_100pct_button_field(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_field_100pct_button_field(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_overfilled(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_overfilled(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_overfilled_dynamic_height(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_overfilled_dynamic_height(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_symmetric_fields(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_symmetric_fields(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_leftAndRight(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_leftAndRight(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_fixed_filled(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_fixed_filled(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_dynamic(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_dynamic(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_labels(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_labels(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_captions(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_captions(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_captions_fixed_size(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_captions_fixed_size(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_captions_fixed_size_and_relative_size(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_captions_fixed_size_and_relative_size(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_captions_fixed_size_and_fixed_size(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_captions_fixed_size_and_fixed_size(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_add_remove_components(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_add_remove_components(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_pctFilled(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_pctFilled(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_underFilled(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_underFilled(new VerticalLayout())));
        layout.addComponent(wrapLayout(layout_basic_test(new HorizontalLayout())));
        layout.addComponent(wrapLayout(layout_basic_test(new VerticalLayout())));
    }

    private Layout wrapLayout(Layout ol) {
        Panel p = new Panel(ol);
        p.setSizeUndefined();
        p.setCaption(ol.getCaption());
        ol.setCaption(null);

        VerticalLayout l = new VerticalLayout();
        l.setSizeUndefined();
        l.addComponent(p);
        // p.setWidth("600px");

        if (ol instanceof AbstractOrderedLayout) {
            layouts.add((AbstractOrderedLayout) ol);
        }
        return l;
    }

    /* LAYOUTS */

    @SuppressWarnings({ "unused", "deprecation" })
    private Layout layout1() {
        HorizontalLayout ol = new HorizontalLayout();
        ol.setHeight("200px");
        ol.setWidth("");
        ol.setCaption("Fixed height (200px) and dynamic width");

        TextField tf = new TextField("100px high TextField, valign: bottom");
        tf.setHeight("100px");
        tf.setWidth("");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_LEFT);

        ListSelect s = new ListSelect("100% high select");
        s.setMultiSelect(true);
        s.setHeight("100%");
        s.setWidth("");
        ol.addComponent(s);

        s = new ListSelect("200 px high select");
        s.setMultiSelect(true);
        s.setHeight("200px");
        s.setWidth("");
        ol.addComponent(s);

        // tf = new TextField("100% high TextField, right/bottom");
        // tf.setHeight("100%");
        // tf.setWidth("");
        // ol.addComponent(tf);
        // ol.setComponentAlignment(tf, AlignmentHandler.ALIGNMENT_RIGHT,
        // AlignmentHandler.ALIGNMENT_BOTTOM);

        // tf = new TextField("100% high, 200px wide TextField");
        // tf.setHeight("100%");
        // tf.setWidth("200px");
        // ol.addComponent(tf);

        return ol;

    }

    @SuppressWarnings({ "unused", "deprecation" })
    private Layout layout2() {
        HorizontalLayout ol = new HorizontalLayout();
        ol.setHeight("70px");
        ol.setWidth("");
        ol.setCaption("Fixed height (50px) and dynamic width");

        TextField tf = new TextField(
                "100px high TextField, valign: bottom, should be partly outside");
        tf.setHeight("100px");
        tf.setWidth("");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_LEFT);

        tf = new TextField(
                "100% high, 50px wide TextField, valign: bottom, should fill full height");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_LEFT);

        Label l = new Label(
                "100% high, 50px wide Label, valign: bottom, does not fill full height, only needed space");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(l);
        ol.setComponentAlignment(l, Alignment.BOTTOM_LEFT);

        ListSelect s = new ListSelect(
                "100% high select, should fit into layout");
        s.setMultiSelect(true);
        s.setHeight("100%");
        s.setWidth("");
        for (int i = 0; i < 10; i++) {
            s.addItem(new Object());
        }

        ol.addComponent(s);

        s = new ListSelect("200 px high select, should be partly outside");
        s.setMultiSelect(true);
        s.setHeight("200px");
        s.setWidth("");
        ol.addComponent(s);

        return ol;
    }

    @SuppressWarnings({ "unused", "deprecation" })
    private Layout layout3() {
        HorizontalLayout ol = new HorizontalLayout();
        ol.setHeight("");
        ol.setWidth("500px");
        ol.setCaption("Fixed width (500px) and dynamic height");
        TextField tf;

        tf = new TextField("100px high TextField, valign: bottom");
        tf.setHeight("100px");
        tf.setWidth("100%");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_LEFT);

        tf = new TextField("100px high TextField, valign: top");
        tf.setHeight("100px");
        tf.setWidth("100%");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.TOP_LEFT);

        tf = new TextField("100% high, 50px wide TextField, valign: bottom");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_LEFT);

        Label l = new Label(
                "100% high, 50px wide Label, valign: bottom, does not fill full height, only needed space");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(l);
        ol.setComponentAlignment(l, Alignment.BOTTOM_LEFT);

        ListSelect s = new ListSelect(
                "100% high select, should fit into layout");
        s.setMultiSelect(true);
        s.setHeight("100%");
        s.setWidth("100%");
        for (int i = 0; i < 10; i++) {
            s.addItem(new Object());
        }

        ol.addComponent(s);

        s = new ListSelect(
                "200 px high select, should make the layout 200px high");
        s.setMultiSelect(true);
        s.setHeight("200px");
        s.setWidth("100%");
        ol.addComponent(s);

        return ol;
    }

    @SuppressWarnings({ "unused", "deprecation" })
    private Layout layout3New() {
        HorizontalLayout ol = new HorizontalLayout();
        ol.setHeight("300px");
        // ol.setWidth("500px");
        ol.setWidth("");
        ol.setCaption("Dynamic width and fixed height(300px)");
        TextField tf;

        tf = new TextField("100px high TextField, valign: bottom");
        tf.setHeight("100px");
        tf.setWidth("100%");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_LEFT);

        tf = new TextField("100px high TextField, valign: top");
        tf.setHeight("100px");
        tf.setWidth("100%");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.TOP_LEFT);

        tf = new TextField("100% high, 50px wide TextField, valign: bottom");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_LEFT);

        Label l = new Label(
                "100% high, 50px wide Label, valign: bottom, does not fill full height, only needed space");
        tf.setHeight("100%");
        tf.setWidth("50px");
        ol.addComponent(l);
        ol.setComponentAlignment(l, Alignment.BOTTOM_LEFT);

        ListSelect s = new ListSelect(
                "100% high select, should fit into layout");
        s.setMultiSelect(true);
        s.setHeight("100%");
        s.setWidth("100%");
        for (int i = 0; i < 10; i++) {
            s.addItem(new Object());
        }

        ol.addComponent(s);

        s = new ListSelect(
                "200 px high select, should make the layout 200px high");
        s.setMultiSelect(true);
        s.setHeight("200px");
        s.setWidth("100%");
        ol.addComponent(s);

        return ol;
    }

    @SuppressWarnings("unused")
    private Layout layout4(AbstractOrderedLayout ol) {
        // ol.setHeight("300px");
        // ol.setWidth("500px");
        ol.setMargin(true);
        ol.setSpacing(true);
        ol.setWidth("");
        ol.setCaption("Dynamic width and dynamic height");
        TextArea tf;

        tf = new TextArea("100% high TextField");
        tf.setCaption(null);
        tf.setRequired(true);
        tf.setValue("100% high Field");
        tf.setHeight("100%");
        tf.setWidth("100px");
        tf.setRows(2);
        ol.addComponent(tf);

        tf = new TextArea("100% high TextField");
        tf.setCaption("100% high TextField");
        tf.setRequired(true);
        tf.setValue("100% high Field");
        tf.setHeight("100%");
        tf.setWidth("100px");
        tf.setRows(2);
        ol.addComponent(tf);

        for (int i = 1; i < 4; i++) {
            int w = i * 100;
            tf = new TextArea("Field " + i);
            tf.setRows(2);
            tf.setValue(w + "px high, " + w + "px wide TextField, valign: "
                    + valignName[i % 3]);
            tf.setWidth(w + "px");
            tf.setHeight(w + "px");
            ol.addComponent(tf);
            if (i % 3 == 0) {
                ol.setComponentAlignment(tf, Alignment.TOP_LEFT);
            } else if (i % 3 == 1) {
                ol.setComponentAlignment(tf, Alignment.MIDDLE_LEFT);
            } else {
                ol.setComponentAlignment(tf, Alignment.BOTTOM_LEFT);
            }

        }

        tf = new TextArea("100% high TextField");
        tf.setValue("100% high 100px wide");
        tf.setRows(2);
        tf.setHeight("100%");
        tf.setWidth("100px");
        ol.addComponent(tf);
        return ol;
    }

    private Layout layout_field_100pct_button_field(AbstractOrderedLayout ol) {
        ol.setHeight("500px");
        ol.setWidth("916px");
        ol.setMargin(false);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight()
                + "px) / layout_field_100pct_button_field");
        TextArea tf;

        tf = new TextArea("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.TOP_LEFT);

        Button b;
        b = new Button("This is a 100%x50% valign middle button");
        b.setSizeFull();
        b.setHeight("50%");
        ol.addComponent(b);
        ol.setExpandRatio(b, 1.0f);
        ol.setComponentAlignment(b, Alignment.MIDDLE_RIGHT);

        tf = new TextArea("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);
        return ol;
    }

    private Layout layout_basic_test(AbstractOrderedLayout ol) {
        ol.setHeight("700px");
        ol.setWidth("900px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight()
                + "px) / layout_basic_test");
        TextArea tf;

        tf = new TextArea("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.TOP_LEFT);

        // Button b;
        // b = new Button("This is a 100%x50% valign middle button");
        // b.setSizeFull();
        // b.setHeight("50%");
        // ol.addComponent(b, 1.0f);
        // ol.setComponentAlignment(b, AlignmentHandler.ALIGNMENT_RIGHT,
        // AlignmentHandler.ALIGNMENT_VERTICAL_CENTER);

        tf = new TextArea("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);
        return ol;
    }

    private Layout layout_symmetric_fields(AbstractOrderedLayout ol) {
        ol.setHeight("900px");
        ol.setWidth("900px");
        ol.setMargin(false);
        ol.setSpacing(false);

        // ol.setWidth("");
        ol.setCaption("Fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight()
                + "px) / layout_symmetric_fields");
        TextArea tf;

        tf = new TextArea("300px x 300px Field");
        tf.setValue("300x300 field");
        tf.setRows(2);
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.TOP_LEFT);

        tf = new TextArea("300px x 300px Field");
        tf.setValue("300x300 field");
        tf.setRows(2);
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.MIDDLE_CENTER);

        tf = new TextArea("300px x 300px Field");
        tf.setValue("300x300 field");
        tf.setRows(2);
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);

        return ol;
    }

    private Layout layout_leftAndRight(AbstractOrderedLayout ol) {
        ol.setHeight("700px");
        ol.setWidth("700px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight()
                + "px) / layout_leftAndRight");
        TextArea tf;

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
        // AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER, valign[i % 3]);
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
        // ol.setComponentAlignment(tf, AlignmentHandler.ALIGNMENT_RIGHT,
        // AlignmentHandler.ALIGNMENT_TOP);
        // ol.addComponent(tf);

        tf = new TextArea("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.TOP_LEFT);

        tf = new TextArea("300px x 300px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("300x300 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("300px");
        tf.setWidth("300px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);
        return ol;
    }

    private Layout layout_fixed_filled(AbstractOrderedLayout ol) {
        ol.setHeight("700px");
        ol.setWidth("700px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Filled with fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight() + "px)");
        TextArea tf;

        tf = new TextArea("60%x100% Field");
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

        tf = new TextArea("60%x60% Field");
        tf.setCaption(null);
        tf.setValue("60% x 60% TextField");
        tf.setWidth("100%");
        tf.setHeight("60%");
        tf.setRequired(true);
        ol.addComponent(tf);
        ol.setExpandRatio(tf, 1f);
        ol.setComponentAlignment(tf, Alignment.MIDDLE_LEFT);
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
        // AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER, valign[i % 3]);
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
        // ol.setComponentAlignment(tf, AlignmentHandler.ALIGNMENT_RIGHT,
        // AlignmentHandler.ALIGNMENT_TOP);
        // ol.addComponent(tf);

        tf = new TextArea("200px x 200px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("200x200 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("200px");
        tf.setWidth("200px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.TOP_LEFT);

        tf = new TextArea("200px x 200px Field");
        // tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setValue("200x200 field");
        tf.setRows(2);
        // tf.setSizeFull();
        tf.setHeight("200px");
        tf.setWidth("200px");
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);
        return ol;
    }

    private Layout layout_overfilled(AbstractOrderedLayout ol) {
        ol.setHeight("300px");
        ol.setWidth("700px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("OverFilled with fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight() + "px)");
        TextArea tf;

        for (int i = 0; i < 5; i++) {
            tf = new TextArea("200x200px Field");
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

    private Layout layout_overfilled_dynamic_height(AbstractOrderedLayout ol) {
        ol.setHeight(null);
        ol.setWidth("700px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("OverFilled with fixed width (" + ol.getWidth()
                + "px) and dynamic height");
        TextArea tf;

        for (int i = 0; i < 10; i++) {
            tf = new TextArea("200x200px Field");
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

    // private Layout layout_add_components(AbstractOrderedLayout ol) {
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

    private Layout layout_add_remove_components(AbstractOrderedLayout ol) {
        ol.setHeight("600px");
        ol.setWidth("600px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight()
                + "px) / layout_add_remove_components");

        for (int i = 0; i < 2; i++) {
            AbstractOrderedLayout inner = createAddRemove(ol, "", "");
            ol.addComponent(inner);
            ol.setComponentAlignment(inner, Alignment.BOTTOM_RIGHT);
        }

        return ol;

    }

    private Layout layout_dynamic(AbstractOrderedLayout ol) {
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

    private Layout layout_captions(AbstractOrderedLayout ol) {
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

    private Layout layout_captions_fixed_size(AbstractOrderedLayout ol) {
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
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);

        tf = new TextField(
                "A long caption which is probably much longer than the field");
        tf.setValue("Undefined width");
        tf.setRequired(true);
        tf.setComponentError(new UserError("123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);

        tf = new TextField(
                "A very long caption which is probably much longer than the field and includes indicators");
        tf.setValue("Undefined width");
        tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setRequired(true);
        tf.setComponentError(new UserError("abc123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);

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

    private Layout layout_captions_fixed_size_and_relative_size(
            AbstractOrderedLayout ol) {
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
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);
        ol.setExpandRatio(tf, 1);

        tf = new TextField(
                "A long caption which is probably much longer than the field");
        tf.setValue("100% wide field, ratio 2");
        tf.setSizeFull();
        tf.setRequired(true);
        tf.setComponentError(new UserError("123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);
        ol.setExpandRatio(tf, 2);

        tf = new TextField(
                "A very long caption which is probably much longer than the field and includes indicators");
        tf.setValue("100% wide field, ratio 3");
        tf.setSizeFull();
        tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setRequired(true);
        tf.setComponentError(new UserError("abc123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);
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

    private Layout layout_captions_fixed_size_and_fixed_size(
            AbstractOrderedLayout ol) {
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
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);

        tf = new TextField(
                "A long caption which is probably much longer than the field");
        tf.setWidth("250px");
        tf.setValue("250px wide field");
        tf.setRequired(true);
        tf.setComponentError(new UserError("123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);

        tf = new TextField(
                "A very long caption which is probably much longer than the field and includes indicators");
        tf.setValue("200px wide field");
        tf.setWidth("200px");
        tf.setIcon(new ThemeResource("icons/16/document-add.png"));
        tf.setRequired(true);
        tf.setComponentError(new UserError("abc123"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.BOTTOM_RIGHT);

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

    private Layout layout_labels(AbstractOrderedLayout ol) {
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
        // ol.setComponentAlignment(l, AlignmentHandler.ALIGNMENT_RIGHT,
        // AlignmentHandler.ALIGNMENT_BOTTOM);

        l = new Label("WTF OMG LOL");
        ol.addComponent(l);
        // ol.setComponentAlignment(l, AlignmentHandler.ALIGNMENT_RIGHT,
        // AlignmentHandler.ALIGNMENT_BOTTOM);

        return ol;

    }

    private AbstractOrderedLayout createAddRemove(AbstractOrderedLayout ol,
            String width, String buttonSuffix) {
        Button b = createAddButton(ol);
        Button wb = createWideAddButton(ol);
        Button r = createRemoveButton(ol, buttonSuffix);
        VerticalLayout inner = new VerticalLayout();
        inner.setCaption("Width: " + width);
        inner.setWidth(width);

        inner.addComponent(b);
        inner.addComponent(wb);
        inner.addComponent(r);

        // inner.setHeight("132px");
        return inner;
    }

    private Button createAddButton(AbstractOrderedLayout ol) {
        Button b = new Button("Add before", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addBefore((AbstractOrderedLayout) event.getButton().getData(),
                        event.getButton().getParent(), "");
            }

        });
        b.setData(ol);

        return b;
    }

    private Button createWideAddButton(AbstractOrderedLayout ol) {
        Button b = new Button("Add 100% before", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                addBefore((AbstractOrderedLayout) event.getButton().getData(),
                        event.getButton().getParent(), "100%");
            }

        });
        b.setData(ol);

        return b;
    }

    private Button createRemoveButton(AbstractOrderedLayout ol, String suffix) {
        Button b = new Button("Remove this " + suffix, new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                remove((AbstractOrderedLayout) event.getButton().getData(),
                        event.getButton().getParent());
            }

        });
        b.setWidth("100%");
        b.setData(ol);

        return b;
    }

    protected void remove(AbstractOrderedLayout ol, Component c) {
        ol.removeComponent(c);

    }

    protected void addBefore(AbstractOrderedLayout ol, Component c, String width) {
        int index = 0;
        Iterator<Component> iter = ol.getComponentIterator();
        while (iter.hasNext()) {
            if (iter.next() == c) {
                break;
            }
            index++;
        }
        AbstractOrderedLayout inner = createAddRemove(ol, width,
                String.valueOf(suffix++));
        ol.addComponent(inner, index);
        if (width.contains("%")) {
            ol.setExpandRatio(inner, 1.0f);
        }

        ol.setComponentAlignment(inner, Alignment.BOTTOM_RIGHT);

    }

    private Layout layout_pctFilled(AbstractOrderedLayout ol) {
        ol.setHeight("600px");
        ol.setWidth("600px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("100 % filled with fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight() + "px)");
        TextArea ta;

        ta = new TextArea();
        ta.setCaption("This one has a caption");
        ta.setValue("60% expand TextField");
        ta.setWidth("100%");
        ta.setHeight("100%");
        // ta.setRequired(true);
        // ta.setComponentError(new UserError("It's broken!"));

        // ta.setHeight("100%");
        // ta.setWidth("100px");
        ta.setRows(2);
        ol.addComponent(ta);
        ol.setExpandRatio(ta, 60);

        ta = new TextArea();
        ta.setValue("100px 100px TextField");
        ta.setWidth("100px");
        ta.setHeight("100px");
        ta.setRows(2);
        ol.addComponent(ta);
        ol.setComponentAlignment(ta, Alignment.MIDDLE_CENTER);

        //

        ta = new TextArea("40%x40% Field");
        // ta.setCaption(null);
        ta.setValue("40% expand (40% height) TextField");
        ta.setWidth("100%");
        ta.setHeight("40%");
        ol.addComponent(ta);
        ol.setExpandRatio(ta, 40);
        // ta.setRequired(true);
        ol.setComponentAlignment(ta, Alignment.BOTTOM_RIGHT);

        ta.setRows(2);

        return ol;
    }

    @SuppressWarnings("unused")
    private Layout layout_pctFilled2(AbstractOrderedLayout ol) {
        ol.setHeight("600px");
        ol.setWidth("600px");
        ol.setMargin(true);
        ol.setSpacing(false);

        // ol.setWidth("");
        ol.setCaption("100 % filled with fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight() + "px)");
        TextArea ta;

        ta = new TextArea();
        // ta.setCaption("This one has a caption");
        ta.setValue("80% x 20% TextField");
        ta.setWidth("80%");
        ta.setHeight("20%");
        // ta.setRequired(true);
        // ta.setComponentError(new UserError("It's broken!"));

        // ta.setHeight("100%");
        // ta.setWidth("100px");
        ta.setRows(2);
        ol.addComponent(ta);
        //

        ta = new TextArea("20%x60% Field");
        ta.setCaption(null);
        ta.setValue("20% x 60% TextField");
        ta.setWidth("20%");
        ta.setHeight("60%");
        // ta.setRequired(true);
        ol.setComponentAlignment(ta, Alignment.BOTTOM_RIGHT);

        ta.setRows(2);
        ol.addComponent(ta);

        return ol;
    }

    private Layout layout_underFilled(AbstractOrderedLayout ol) {
        ol.setHeight("700px");
        ol.setWidth("700px");
        ol.setMargin(true);
        ol.setSpacing(true);

        // ol.setWidth("");
        ol.setCaption("Underfilled with fixed width (" + ol.getWidth()
                + "px) and fixed height (" + ol.getHeight() + "px)");
        TextArea ta;

        ta = new TextArea("60%x100% Field");
        ta.setCaption("Short capt");
        ta.setValue("60% x 100% TextField");
        ta.setWidth("60%");
        ta.setHeight("100%");
        ta.setRequired(true);
        ta.setRows(2);

        ol.addComponent(ta);
        ol.setComponentAlignment(ta, Alignment.MIDDLE_CENTER);

        ta = new TextArea("200px x 200px Field");
        // ta.setIcon(new ThemeResource("icons/16/document-add.png"));
        ta.setValue("200x200 field");
        ta.setRows(2);
        // ta.setSizeFull();
        ta.setHeight("200px");
        ta.setWidth("200px");
        ol.addComponent(ta);
        ol.setComponentAlignment(ta, Alignment.TOP_LEFT);

        ta = new TextArea("200px x 200px Field");
        // ta.setIcon(new ThemeResource("icons/16/document-add.png"));
        ta.setValue("200x200 field");
        ta.setRows(2);
        // ta.setSizeFull();
        ta.setHeight("200px");
        ta.setWidth("200px");
        ol.addComponent(ta);
        ol.setComponentAlignment(ta, Alignment.BOTTOM_RIGHT);
        return ol;
    }

}
