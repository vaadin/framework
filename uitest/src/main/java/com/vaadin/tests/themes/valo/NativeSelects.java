package com.vaadin.tests.themes.valo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.TwinColSelect;

public class NativeSelects extends VerticalLayout implements View {
    public NativeSelects() {
        setSpacing(false);

        Label h1 = new Label("Selects");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        addComponent(row);

        NativeSelect select = new NativeSelect("Drop Down Select");
        row.addComponent(select);

        ListSelect list = new ListSelect("List Select");
        list.setNewItemsAllowed(true);
        row.addComponent(list);

        TwinColSelect tcs = new TwinColSelect("TwinCol Select");
        tcs.setLeftColumnCaption("Left Column");
        tcs.setRightColumnCaption("Right Column");
        tcs.setNewItemsAllowed(true);
        row.addComponent(tcs);

        TwinColSelect tcs2 = new TwinColSelect("Sized TwinCol Select");
        tcs2.setLeftColumnCaption("Left Column");
        tcs2.setRightColumnCaption("Right Column");
        tcs2.setNewItemsAllowed(true);
        tcs2.setWidth("280px");
        tcs2.setHeight("200px");
        row.addComponent(tcs2);

        for (int i = 1; i <= 10; i++) {
            select.addItem("Option " + i);
            list.addItem("Option " + i);
            tcs.addItem("Option " + i);
            tcs2.addItem("Option " + i);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
