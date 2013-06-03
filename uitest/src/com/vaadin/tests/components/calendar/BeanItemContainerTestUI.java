/**
 * Copyright 2013 Vaadin Ltd.
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
package com.vaadin.tests.components.calendar;

import java.util.Arrays;
import java.util.Date;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.components.calendar.ContainerEventProvider;
import com.vaadin.ui.components.calendar.event.BasicEvent;

public class BeanItemContainerTestUI extends UI {

    private Calendar calendar;

    private Table table;

    private BeanItemContainer<BasicEvent> events = new BeanItemContainer<BasicEvent>(
            BasicEvent.class);

    @SuppressWarnings("deprecation")
    @Override
    protected void init(VaadinRequest request) {
        VerticalSplitPanel content = new VerticalSplitPanel();
        content.setSizeFull();
        setContent(content);

        // Create Calendar
        calendar = new Calendar();
        calendar.setImmediate(true);
        calendar.setSizeFull();
        calendar.setContainerDataSource(events);
        calendar.setStartDate(new Date(100, 1, 1));
        calendar.setEndDate(new Date(100, 2, 1));

        content.addComponent(calendar);

        // Add event table connected to same data source
        table = createTable();
        table.setContainerDataSource(events);
        table.setVisibleColumns(new Object[] { "caption", "description",
                "start", "end" });
        content.addComponent(table);
    }

    /**
     * Creates a table with some actions
     * 
     * @return
     */
    private Table createTable() {
        Table table = new Table();
        table.setSizeFull();
        table.addActionHandler(new Action.Handler() {

            private final Action ADD = new Action("Add event");
            private final Action EDIT = new Action("Edit event");
            private final Action REMOVE = new Action("Remove event");

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                if (action == ADD) {
                    BasicEvent event = new BasicEvent();
                    event.setStart(new Date(100, 1, 1));
                    event.setEnd(new Date(100, 1, 1));
                    editEvent(event);
                } else if (action == EDIT) {
                    editEvent((BasicEvent) target);
                } else if (action == REMOVE) {
                    events.removeItem(target);
                }
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                if (target == null) {
                    return new Action[] { ADD };
                } else {
                    return new Action[] { EDIT, REMOVE };
                }
            }
        });
        return table;
    }

    /**
     * Opens up a modal dialog window where an event can be modified
     * 
     * @param event
     *            The event to modify
     */
    private void editEvent(final BasicEvent event) {
        Window modal = new Window("Add event");
        modal.setModal(true);
        modal.setResizable(false);
        modal.setDraggable(false);
        modal.setWidth("300px");
        final FieldGroup fieldGroup = new FieldGroup();

        FormLayout formLayout = new FormLayout();
        TextField captionField = new TextField("Caption");
        captionField.setImmediate(true);
        TextField descriptionField = new TextField("Description");
        descriptionField.setImmediate(true);
        DateField startField = new DateField("Start");
        startField.setResolution(Resolution.MINUTE);
        startField.setImmediate(true);
        DateField endField = new DateField("End");
        endField.setImmediate(true);
        endField.setResolution(Resolution.MINUTE);

        formLayout.addComponent(captionField);
        formLayout.addComponent(descriptionField);
        formLayout.addComponent(startField);
        formLayout.addComponent(endField);

        fieldGroup.bind(captionField, ContainerEventProvider.CAPTION_PROPERTY);
        fieldGroup.bind(descriptionField,
                ContainerEventProvider.DESCRIPTION_PROPERTY);
        fieldGroup.bind(startField, ContainerEventProvider.STARTDATE_PROPERTY);
        fieldGroup.bind(endField, ContainerEventProvider.ENDDATE_PROPERTY);

        fieldGroup.setItemDataSource(new BeanItem<BasicEvent>(event, Arrays
                .asList(ContainerEventProvider.CAPTION_PROPERTY,
                        ContainerEventProvider.DESCRIPTION_PROPERTY,
                        ContainerEventProvider.STARTDATE_PROPERTY,
                        ContainerEventProvider.ENDDATE_PROPERTY)));
        modal.setContent(formLayout);
        modal.addCloseListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                // Commit changes to bean
                try {
                    fieldGroup.commit();
                } catch (CommitException e1) {
                    e1.printStackTrace();
                }

                if (events.containsId(event)) {
                    /*
                     * BeanItemContainer does not notify container listeners
                     * when the bean changes so we need to trigger a
                     * ItemSetChange event
                     */
                    BasicEvent dummy = new BasicEvent();
                    events.addBean(dummy);
                    events.removeItem(dummy);

                } else {
                    events.addBean(event);
                }
            }
        });
        getUI().addWindow(modal);
    }
}
