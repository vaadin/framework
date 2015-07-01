package com.vaadin.tests.tickets;

import java.util.Iterator;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.SystemError;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.AlignmentInfo.Bits;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.AlignmentHandler;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket1710 extends com.vaadin.server.LegacyApplication {

    @Override
    public void init() {

        setTheme("tests-tickets");

        VerticalLayout lo = new VerticalLayout();
        setMainWindow(new LegacyWindow("#1710", lo));
        lo.setMargin(true);
        lo.setSpacing(true);
        lo.setWidth("100%");

        // Hiding controls
        HorizontalLayout hidingControls = new HorizontalLayout();
        lo.addComponent(hidingControls);

        // OrderedLayout
        final VerticalLayout orderedLayout = new VerticalLayout();
        LayoutTestingPanel oltp = new LayoutTestingPanel("OrderedLayout",
                orderedLayout);
        CheckBox cb = new CheckBox("OrderedLayout",
                new MethodProperty<Boolean>(oltp, "visible"));
        cb.setImmediate(true);
        hidingControls.addComponent(cb);
        lo.addComponent(oltp);
        orderedLayout.setSpacing(false);
        addFields(orderedLayout);

        // GridLayout
        GridLayout grid = new GridLayout(1, 1);
        Panel g1tp = new LayoutTestingPanel("Gridlayout with 1 column", grid);
        cb = new CheckBox("GridLayout (1col)", new MethodProperty<Boolean>(
                g1tp, "visible"));
        cb.setImmediate(true);
        hidingControls.addComponent(cb);
        g1tp.setVisible(false);
        lo.addComponent(g1tp);
        grid.setSpacing(true);
        addFields(grid);
        GridLayout grid2 = new GridLayout(2, 1);
        Panel g2tp = new LayoutTestingPanel("Gridlayout with 2 columns", grid2);
        cb = new CheckBox("GridLayout (2cols)", new MethodProperty<Boolean>(
                g2tp, "visible"));
        cb.setImmediate(true);
        hidingControls.addComponent(cb);
        g2tp.setVisible(false);
        lo.addComponent(g2tp);
        grid2.setSpacing(true);
        addFields(grid2);

        // ExpandLayout
        VerticalLayout el = new VerticalLayout();
        Panel elp = new LayoutTestingPanel(
                "ExpandLayout width first component expanded", el);
        cb = new CheckBox("ExpandLayout (vertical)",
                new MethodProperty<Boolean>(elp, "visible"));
        cb.setImmediate(true);
        hidingControls.addComponent(cb);
        elp.setVisible(false);
        el.setHeight("700px");
        addFields(el);
        Component firstComponent = el.getComponentIterator().next();
        firstComponent.setSizeFull();
        el.setExpandRatio(firstComponent, 1);
        lo.addComponent(elp);
        HorizontalLayout elh = new HorizontalLayout();
        Panel elhp = new LayoutTestingPanel(
                "ExpandLayout width first component expanded; horizontal", elh);
        cb = new CheckBox("ExpandLayout (horizontal)",
                new MethodProperty<Boolean>(elhp, "visible"));
        cb.setImmediate(true);
        hidingControls.addComponent(cb);
        elhp.setVisible(false);
        elh.setWidth("2000px");
        elh.setHeight("100px");
        addFields(elh);
        Component firstComponentElh = elh.getComponentIterator().next();
        firstComponentElh.setSizeFull();
        elh.setExpandRatio(firstComponentElh, 1);
        lo.addComponent(elhp);

        // CustomLayout
        VerticalLayout cl = new VerticalLayout();
        Panel clp = new LayoutTestingPanel("CustomLayout", cl);
        cb = new CheckBox("CustomLayout", new MethodProperty<Boolean>(clp,
                "visible"));
        cb.setImmediate(true);
        hidingControls.addComponent(cb);
        clp.setVisible(false);
        lo.addComponent(clp);
        cl.addComponent(new Label("<<< Add customlayout testcase here >>>"));

        // Form
        VerticalLayout formPanelLayout = new VerticalLayout();
        formPanelLayout.setMargin(true);
        Panel formPanel = new Panel("Form", formPanelLayout);
        cb = new CheckBox("Form", new MethodProperty<Boolean>(formPanel,
                "visible"));
        cb.setImmediate(true);
        hidingControls.addComponent(cb);
        formPanel.setVisible(false);
        formPanelLayout.addComponent(getFormPanelExample());
        lo.addComponent(formPanel);

        for (Iterator<Component> i = hidingControls.getComponentIterator(); i
                .hasNext();) {
            ((AbstractComponent) i.next()).setImmediate(true);
        }

    }

    private Form getFormPanelExample() {
        Form f = new Form();
        f.setCaption("Test form");
        CheckBox fb2 = new CheckBox("Test button", true);
        fb2.setComponentError(new SystemError("Test error"));
        f.addField("fb2", fb2);
        TextField ft1 = new TextField("With caption");
        ft1.setComponentError(new SystemError("Error"));
        f.addField("ft1", ft1);
        TextField ft2 = new TextField();
        ft2.setComponentError(new SystemError("Error"));
        ft2.setValue("Without caption");
        f.addField("ft2", ft2);
        TextField ft3 = new TextField("With caption and required");
        ft3.setComponentError(new SystemError("Error"));
        ft3.setRequired(true);
        f.addField("ft3", ft3);
        return f;
    }

    private void addFields(ComponentContainer lo) {
        Button button = new Button("Test button");
        button.setComponentError(new SystemError("Test error"));
        lo.addComponent(button);

        CheckBox b2 = new CheckBox("Test button");
        b2.setComponentError(new SystemError("Test error"));
        lo.addComponent(b2);

        TextField t1 = new TextField("With caption");
        t1.setComponentError(new SystemError("Error"));
        lo.addComponent(t1);

        TextField t2 = new TextField("With caption and required");
        t2.setComponentError(new SystemError("Error"));
        t2.setRequired(true);
        lo.addComponent(t2);

        TextField t3 = new TextField();
        t3.setValue("Without caption");
        t3.setComponentError(new SystemError("Error"));
        lo.addComponent(t3);

        lo.addComponent(new TextField("Textfield with no error in it"));

        TextField tt1 = new TextField("100% wide Textfield with no error in it");
        tt1.setWidth("100%");
        lo.addComponent(tt1);

        TextField tt2 = new TextField();
        tt2.setWidth("100%");
        tt2.setValue("100% wide Textfield with no error in it and no caption");
        lo.addComponent(tt2);

        TextField t4 = new TextField();
        t4.setValue("Without caption, With required");
        t4.setComponentError(new SystemError("Error"));
        t4.setRequired(true);
        lo.addComponent(t4);

        TextField t5 = new TextField();
        t5.setValue("Without caption,  WIDE");
        t5.setComponentError(new SystemError("Error"));
        t5.setWidth("100%");
        lo.addComponent(t5);

        TextField t6 = new TextField();
        t6.setValue("Without caption, With required, WIDE");
        t6.setComponentError(new SystemError("Error"));
        t6.setRequired(true);
        t6.setWidth("100%");
        lo.addComponent(t6);

        TextField t7 = new TextField();
        t7.setValue("With icon and required and icon");
        t7.setComponentError(new SystemError("Error"));
        t7.setRequired(true);
        t7.setIcon(new ThemeResource("../runo/icons/16/ok.png"));
        lo.addComponent(t7);

        DateField d1 = new DateField(
                "Datefield with caption and icon, next one without caption");
        d1.setComponentError(new SystemError("Error"));
        d1.setRequired(true);
        d1.setIcon(new ThemeResource("../runo/icons/16/ok.png"));
        lo.addComponent(d1);

        DateField d2 = new DateField();
        d2.setComponentError(new SystemError("Error"));
        d2.setRequired(true);
        lo.addComponent(d2);
    }

    public class LayoutTestingPanel extends Panel {

        Layout testedLayout;

        HorizontalLayout controls = new HorizontalLayout();
        CheckBox marginLeft = new CheckBox("m-left", false);
        CheckBox marginRight = new CheckBox("m-right", false);
        CheckBox marginTop = new CheckBox("m-top", false);
        CheckBox marginBottom = new CheckBox("m-bottom", false);
        CheckBox spacing = new CheckBox("spacing", false);
        VerticalLayout testPanelLayout = new VerticalLayout();

        LayoutTestingPanel(String caption, Layout layout) {
            super(caption);
            VerticalLayout internalLayout = new VerticalLayout();
            internalLayout.setWidth("100%");
            setContent(internalLayout);
            testedLayout = layout;
            testPanelLayout.setWidth("100%");
            VerticalLayout controlWrapperLayout = new VerticalLayout();
            controlWrapperLayout.setMargin(true);
            Panel controlWrapper = new Panel(controlWrapperLayout);
            controlWrapperLayout.addComponent(controls);
            controlWrapper.setWidth("100%");
            controlWrapper.setStyleName("controls");
            internalLayout.addComponent(controlWrapper);
            Panel testPanel = new Panel(testPanelLayout);
            testPanel.setStyleName("testarea");
            testPanelLayout.addComponent(testedLayout);
            internalLayout.addComponent(testPanel);
            internalLayout.setMargin(true);
            internalLayout.setSpacing(true);
            controls.setSpacing(true);
            controls.setMargin(false);
            controls.addComponent(new Label("width"));
            controls.addComponent(new TextField(new MethodProperty<Float>(
                    testedLayout, "width")));
            CheckBox widthPercentsCheckBox = new CheckBox("%",
                    new MethodProperty<Boolean>(this, "widthPercents"));
            widthPercentsCheckBox.setImmediate(true);
            controls.addComponent(widthPercentsCheckBox);
            controls.addComponent(new Label("height"));
            controls.addComponent(new TextField(new MethodProperty<Float>(
                    testedLayout, "height")));
            CheckBox heightPercentsCheckBox = new CheckBox("%",
                    new MethodProperty<Boolean>(this, "heightPercents"));
            heightPercentsCheckBox.setImmediate(true);
            controls.addComponent(heightPercentsCheckBox);
            controls.addComponent(marginLeft);
            controls.addComponent(marginRight);
            controls.addComponent(marginTop);
            controls.addComponent(marginBottom);
            if (testedLayout instanceof Layout.SpacingHandler) {
                controls.addComponent(spacing);
            }

            Property.ValueChangeListener marginSpacingListener = new Property.ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    updateMarginsAndSpacing();
                }
            };

            marginBottom.addListener(marginSpacingListener);
            marginTop.addListener(marginSpacingListener);
            marginLeft.addListener(marginSpacingListener);
            marginRight.addListener(marginSpacingListener);
            spacing.addListener(marginSpacingListener);
            updateMarginsAndSpacing();

            addAlignmentControls();

            testedLayout.setStyleName("tested-layout");
            setStyleName("layout-testing-panel");

            for (Iterator<Component> i = controls.getComponentIterator(); i
                    .hasNext();) {
                ((AbstractComponent) i.next()).setImmediate(true);
            }
        }

        @SuppressWarnings("deprecation")
        private void addAlignmentControls() {
            if (!(testedLayout instanceof Layout.AlignmentHandler)) {
                return;
            }
            @SuppressWarnings("unused")
            final Layout.AlignmentHandler ah = (AlignmentHandler) testedLayout;

            final NativeSelect vAlign = new NativeSelect();
            final NativeSelect hAlign = new NativeSelect();
            controls.addComponent(new Label("component alignment"));
            controls.addComponent(hAlign);
            controls.addComponent(vAlign);
            hAlign.setNullSelectionAllowed(false);
            vAlign.setNullSelectionAllowed(false);

            vAlign.addItem(new Integer(Bits.ALIGNMENT_TOP));
            vAlign.setItemCaption(new Integer(Bits.ALIGNMENT_TOP), "top");
            vAlign.addItem(new Integer(Bits.ALIGNMENT_VERTICAL_CENTER));
            vAlign.setItemCaption(new Integer(Bits.ALIGNMENT_VERTICAL_CENTER),
                    "center");
            vAlign.addItem(new Integer(Bits.ALIGNMENT_BOTTOM));
            vAlign.setItemCaption(new Integer(Bits.ALIGNMENT_BOTTOM), "bottom");

            hAlign.addItem(new Integer(Bits.ALIGNMENT_LEFT));
            hAlign.setItemCaption(new Integer(Bits.ALIGNMENT_LEFT), "left");
            hAlign.addItem(new Integer(Bits.ALIGNMENT_HORIZONTAL_CENTER));
            hAlign.setItemCaption(
                    new Integer(Bits.ALIGNMENT_HORIZONTAL_CENTER), "center");
            hAlign.addItem(new Integer(Bits.ALIGNMENT_RIGHT));
            hAlign.setItemCaption(new Integer(Bits.ALIGNMENT_RIGHT), "right");

            Property.ValueChangeListener alignmentChangeListener = new Property.ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    Integer h = ((Integer) hAlign.getValue()).intValue();
                    int v = ((Integer) vAlign.getValue()).intValue();

                    updateAlignments(new Alignment(h + v));
                }

            };

            hAlign.setValue(new Integer(Bits.ALIGNMENT_LEFT));
            vAlign.addListener(alignmentChangeListener);
            hAlign.addListener(alignmentChangeListener);
            vAlign.setValue(new Integer(Bits.ALIGNMENT_TOP));

            controls.addComponent(new Label("layout alignment"));
            final NativeSelect lAlign = new NativeSelect();
            controls.addComponent(lAlign);
            lAlign.setNullSelectionAllowed(false);
            lAlign.addItem(new Integer(Bits.ALIGNMENT_LEFT));
            lAlign.setItemCaption(new Integer(Bits.ALIGNMENT_LEFT), "left");
            lAlign.addItem(new Integer(Bits.ALIGNMENT_HORIZONTAL_CENTER));
            lAlign.setItemCaption(
                    new Integer(Bits.ALIGNMENT_HORIZONTAL_CENTER), "center");
            lAlign.addItem(new Integer(Bits.ALIGNMENT_RIGHT));
            lAlign.setItemCaption(new Integer(Bits.ALIGNMENT_RIGHT), "right");

            lAlign.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    testPanelLayout.setComponentAlignment(
                            testedLayout,
                            new Alignment(((Integer) lAlign.getValue())
                                    .intValue() + Bits.ALIGNMENT_TOP));
                }
            });
        }

        private void updateAlignments(Alignment a) {
            for (Iterator<Component> i = testedLayout.getComponentIterator(); i
                    .hasNext();) {
                ((Layout.AlignmentHandler) testedLayout).setComponentAlignment(
                        i.next(), a);
            }
        }

        private void updateMarginsAndSpacing() {
            if (testedLayout instanceof Layout.MarginHandler) {
                ((Layout.MarginHandler) testedLayout).setMargin(new MarginInfo(
                        marginTop.getValue().booleanValue(), marginRight
                                .getValue().booleanValue(), marginBottom
                                .getValue().booleanValue(), marginLeft
                                .getValue().booleanValue()));
            }
            if (testedLayout instanceof Layout.SpacingHandler) {
                ((Layout.SpacingHandler) testedLayout).setSpacing(spacing
                        .getValue().booleanValue());
            }
        }

    }
}
