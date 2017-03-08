/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.TableDragMode;

public class DragAndDropDisable extends AbstractReindeerTestUI {

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
            enabled.addValueChangeListener(
                    event -> dnd.setEnabled(event.getValue()));

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
            enabled.addValueChangeListener(
                    event -> dnd.setEnabled(event.getValue()));

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
            enabled.addValueChangeListener(
                    event -> tbl.setEnabled(event.getValue()));
        }
    }

    @Override
    protected String getTestDescription() {
        return "DragAndDropWrapper must be disableable";
    }
}
