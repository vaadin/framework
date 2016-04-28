/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.components.table;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.TargetDetailsImpl;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.VerticalLayout;

/**
 * Test UI for table as a drop target: AbstractSelectTargetDetails should
 * provide getMouseEvent() method.
 * 
 * @author Vaadin Ltd
 */
public class DndTableTargetDetails extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        createSourceTable();

        Table target = new Table();
        BeanItemContainer<TestBean> container = new BeanItemContainer<TestBean>(
                TestBean.class);
        container.addBean(new TestBean("target-item"));
        target.setContainerDataSource(container);
        target.setPageLength(1);
        target.addStyleName("target");
        target.setWidth(100, Unit.PERCENTAGE);
        target.setDropHandler(new TestDropHandler());
        addComponent(target);
    }

    protected void createSourceTable() {
        Table table = new Table();
        table.setPageLength(1);
        table.setDragMode(TableDragMode.ROW);
        table.setWidth(100, Unit.PERCENTAGE);
        BeanItemContainer<TestBean> container = new BeanItemContainer<TestBean>(
                TestBean.class);
        container.addBean(new TestBean("item"));
        table.setContainerDataSource(container);
        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "Mouse details should be available for AbstractSelectTargetDetails DnD when table is a target";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13416;
    }

    protected static class TestDropHandler implements DropHandler {

        public TestDropHandler() {
        }

        @Override
        public void drop(DragAndDropEvent event) {
            TargetDetailsImpl details = (TargetDetailsImpl) event
                    .getTargetDetails();
            MouseEventDetails mouseDetails = details.getMouseEvent();

            VerticalLayout layout = (VerticalLayout) details.getTarget()
                    .getUI().getContent();

            Label name = new Label("Button name="
                    + mouseDetails.getButtonName());
            name.addStyleName("dnd-button-name");
            layout.addComponent(name);
            if (mouseDetails.isCtrlKey()) {
                name.addStyleName("ctrl");
            }
            if (mouseDetails.isAltKey()) {
                name.addStyleName("alt");
            }
            if (mouseDetails.isShiftKey()) {
                name.addStyleName("shift");
            }

            layout.addComponent(name);
        }

        @Override
        public AcceptCriterion getAcceptCriterion() {
            return AcceptAll.get();
        }

    }

    public static class TestBean {
        private String name;

        public TestBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
