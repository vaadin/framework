package com.itmill.toolkit.tests.tickets;

import java.util.LinkedList;

import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.terminal.SystemError;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ComponentContainer;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket1710 extends com.itmill.toolkit.Application {

    LinkedList listOfAllFields = new LinkedList();

    public void init() {

        OrderedLayout lo = new OrderedLayout();
        setMainWindow(new Window("#1710", lo));
        lo.setMargin(true);
        lo.setSpacing(true);

        // OrderedLayout
        OrderedLayout orderedVertical = new OrderedLayout();
        lo.addComponent(new Panel("OrderedLayout Vertical", orderedVertical));
        orderedVertical.setSpacing(true);
        addFields(orderedVertical);
        OrderedLayout orderedHorizontal = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        lo
                .addComponent(new Panel("OrderedLayout Horizontal",
                        orderedHorizontal));
        orderedHorizontal.setSpacing(true);
        addFields(orderedHorizontal);

        // GridLayout
        GridLayout grid = new GridLayout(1, 1);
        lo.addComponent(new Panel("Gridlayout with 1 column", grid));
        grid.setSpacing(true);
        addFields(grid);

        // GridLayout
        GridLayout grid2 = new GridLayout(2, 1);
        lo.addComponent(new Panel("Gridlayout with 2 columns", grid2));
        grid2.setSpacing(true);
        addFields(grid2);

        // ExpandLayout
        ExpandLayout el = new ExpandLayout();
        Panel elp = new Panel("ExpandLayout width first component expanded", el);
        el.setHeight(700);
        addFields(el);
        Component firstComponent = (Component) el.getComponentIterator().next();
        firstComponent.setSizeFull();
        el.expand(firstComponent);
        lo.addComponent(elp);
        ExpandLayout elh = new ExpandLayout(ExpandLayout.ORIENTATION_HORIZONTAL);
        Panel elhp = new Panel(
                "ExpandLayout width first component expanded; horizontal", elh);
        elhp.setScrollable(true);
        elh.setWidth(2000);
        elh.setHeight(100);
        addFields(elh);
        Component firstComponentElh = (Component) elh.getComponentIterator()
                .next();
        firstComponentElh.setSizeFull();
        elh.expand(firstComponentElh);
        lo.addComponent(elhp);

        // CustomLayout
        Panel clp = new Panel("CustomLayout");
        lo.addComponent(clp);
        clp.addComponent(new Label("<<< Add customlayout testcase here >>>"));

        // Form
        Panel formPanel = new Panel("Form");
        formPanel.addComponent(getFormPanelExample());
        lo.addComponent(formPanel);

    }

    private Form getFormPanelExample() {
        Form f = new Form();
        f.setCaption("Test form");
        Button fb1 = new Button("Test button");
        fb1.setComponentError(new SystemError("Test error"));
        f.addField("fb1", fb1);
        Button fb2 = new Button("Test button");
        fb2.setComponentError(new SystemError("Test error"));
        fb2.setSwitchMode(true);
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

        Button b2 = new Button("Test button");
        b2.setComponentError(new SystemError("Test error"));
        b2.setSwitchMode(true);
        lo.addComponent(b2);

        TextField t1 = new TextField("With caption");
        t1.setComponentError(new SystemError("Error"));
        lo.addComponent(t1);

        TextField t2 = new TextField("With caption and required");
        t2.setComponentError(new SystemError("Error"));
        t2.setRequired(true);
        lo.addComponent(t2);

        TextField t3 = new TextField();
        t3.setValue("With caption");
        t3.setComponentError(new SystemError("Error"));
        lo.addComponent(t3);

        TextField t4 = new TextField();
        t4.setValue("With caption and required");
        t4.setComponentError(new SystemError("Error"));
        t4.setRequired(true);
        lo.addComponent(t4);

        TextField t5 = new TextField();
        t5.setValue("With caption - WIDE");
        t5.setComponentError(new SystemError("Error"));
        t5.setWidth(100);
        t5.setWidthUnits(Sizeable.UNITS_PERCENTAGE);
        lo.addComponent(t5);

        TextField t6 = new TextField();
        t6.setValue("With caption and required - WIDE");
        t6.setComponentError(new SystemError("Error"));
        t6.setRequired(true);
        t6.setWidth(100);
        t6.setWidthUnits(Sizeable.UNITS_PERCENTAGE);
        lo.addComponent(t6);

        TextField t7 = new TextField();
        t7.setValue("With caption and required and icon");
        t7.setComponentError(new SystemError("Error"));
        t7.setRequired(true);
        t7.setIcon(new ThemeResource("icons/16/ok.png"));
        lo.addComponent(t7);
    }
}
