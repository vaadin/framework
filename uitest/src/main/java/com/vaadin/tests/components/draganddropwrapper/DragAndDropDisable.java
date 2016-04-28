package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;

public class DragAndDropDisable extends AbstractTestUI {

    @Override
    protected Integer getTicketNumber() {
        return 11801;
    }

    @Override
    protected void setup(VaadinRequest request) {
        {
            final Panel p = new Panel("Drag here");
            addComponent(p);

            final CssLayout layout = new CssLayout();
            layout.setId("csslayout-1");
            layout.setHeight("100px");

            final DragAndDropWrapper dnd = new DragAndDropWrapper(layout);
            dnd.setId("ddwrapper-1");
            p.setContent(dnd);

            final CheckBox enabled = new CheckBox("Enabled", true);
            addComponent(enabled);
            enabled.setImmediate(true);
            enabled.addListener(new ValueChangeListener() {

                @Override
                public void valueChange(ValueChangeEvent event) {
                    dnd.setEnabled(enabled.booleanValue());
                }
            });

            dnd.setDropHandler(new DropHandler() {

                @Override
                public AcceptCriterion getAcceptCriterion() {
                    return AcceptAll.get();
                }

                @Override
                public void drop(DragAndDropEvent event) {
                    layout.addComponent(new Label("You dropped something!"));
                }
            });

            dnd.setDragStartMode(DragStartMode.COMPONENT);
        }

        {
            final Panel p = new Panel("Drag here");
            addComponent(p);

            final CssLayout layout = new CssLayout();
            layout.setId("csslayout-2");
            layout.setHeight("100px");

            final DragAndDropWrapper dnd = new DragAndDropWrapper(layout);
            dnd.setId("ddwrapper-2");
            p.setContent(dnd);

            final CheckBox enabled = new CheckBox("Enabled", true);
            addComponent(enabled);
            enabled.setImmediate(true);
            enabled.addListener(new ValueChangeListener() {

                @Override
                public void valueChange(ValueChangeEvent event) {
                    dnd.setEnabled(enabled.booleanValue());
                }
            });

            dnd.setDropHandler(new DropHandler() {

                @Override
                public AcceptCriterion getAcceptCriterion() {
                    return AcceptAll.get();
                }

                @Override
                public void drop(DragAndDropEvent event) {
                    layout.addComponent(new Label("You dropped something!"));
                }
            });

            dnd.setDragStartMode(DragStartMode.COMPONENT);
        }

        {
            final Table tbl = new Table();
            tbl.addContainerProperty("column", String.class,
                    "drag/drop to/from here");
            for (int i = 0; i < 5; i++) {
                tbl.addItem();
            }
            addComponent(tbl);
            tbl.setDragMode(TableDragMode.ROW);
            tbl.setDropHandler(new DropHandler() {

                @Override
                public AcceptCriterion getAcceptCriterion() {
                    return AcceptAll.get();
                }

                @Override
                public void drop(DragAndDropEvent event) {
                    tbl.getItem(tbl.addItem()).getItemProperty("column")
                            .setValue("You dropped something");
                }
            });
            final CheckBox enabled = new CheckBox("Enabled", true);
            addComponent(enabled);
            enabled.setImmediate(true);
            enabled.addListener(new ValueChangeListener() {

                @Override
                public void valueChange(ValueChangeEvent event) {
                    tbl.setEnabled(enabled.booleanValue());
                }
            });
        }
    }

    @Override
    protected String getTestDescription() {
        return "DragAndDropWrapper must be disableable";
    }
}
