/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.components.absolutelayout;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

/**
 * Tests how AbsoluteLayout handles relative sized contents.
 * 
 * @author Vaadin Ltd
 */
@Theme("tests-tickets")
public class AbsoluteLayoutRelativeSizeContent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout level1 = new HorizontalLayout(
                createComparisonTableOnFixed(), createTableOnFixed(),
                createHalfTableOnFixed(),
                createHalfTableAndFixedTableOnFixed(), createHalfTableOnFull());
        level1.setSpacing(true);
        level1.setWidth(100, Unit.PERCENTAGE);
        level1.setExpandRatio(
                level1.getComponent(level1.getComponentCount() - 1), 1);
        level1.setMargin(new MarginInfo(true, false, false, false));

        HorizontalLayout level2 = new HorizontalLayout(createFullOnFixed(),
                createFullOnFull());
        level2.setSpacing(true);
        level2.setWidth(100, Unit.PERCENTAGE);
        level2.setExpandRatio(
                level2.getComponent(level2.getComponentCount() - 1), 1);
        level2.setMargin(new MarginInfo(true, false, false, false));

        addComponent(level1);
        addComponent(level2);
    }

    /**
     * Creates an {@link AbsoluteLayout} of fixed size that contains a
     * full-sized {@link Table} that has been forced to full size with css.
     * Represents the workaround given for this ticket.
     * 
     * @return the created layout
     */
    private Component createComparisonTableOnFixed() {
        AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setWidth(200, Unit.PIXELS);
        absoluteLayout.setHeight(200, Unit.PIXELS);
        absoluteLayout.setCaption("comparison table in full size");

        Table table = new Table();
        table.setSizeFull();
        table.setId("comparison-table");
        absoluteLayout.addComponent(table, "top:0;bottom:0;left:0;right:0;");
        return absoluteLayout;
    }

    /**
     * Creates an {@link AbsoluteLayout} of fixed size that contains a
     * full-sized {@link Table}.
     * 
     * @return the created layout
     */
    private Component createTableOnFixed() {
        AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setWidth(200, Unit.PIXELS);
        absoluteLayout.setHeight(200, Unit.PIXELS);
        absoluteLayout.setCaption("full-sized table expected");

        Table table = new Table();
        table.setSizeFull();
        table.setId("full-table");
        absoluteLayout.addComponent(table);
        return absoluteLayout;
    }

    /**
     * Creates an {@link AbsoluteLayout} of fixed size that contains a
     * half-sized {@link Table}.
     * 
     * @return the created layout
     */
    private Component createHalfTableOnFixed() {
        AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setWidth(200, Unit.PIXELS);
        absoluteLayout.setHeight(200, Unit.PIXELS);
        absoluteLayout.setCaption("half-sized table expected");

        Table table = new Table();
        table.setWidth(50, Unit.PERCENTAGE);
        table.setHeight(50, Unit.PERCENTAGE);
        table.setId("half-table");
        absoluteLayout.addComponent(table);
        return absoluteLayout;
    }

    /**
     * Creates an {@link AbsoluteLayout} of fixed size that contains a
     * half-sized {@link Table} and a fixed size {@link Table}.
     *
     * @return the created layout
     */
    private Component createHalfTableAndFixedTableOnFixed() {
        AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setWidth(200, Unit.PIXELS);
        absoluteLayout.setHeight(200, Unit.PIXELS);
        absoluteLayout.setCaption("half-sized and tiny expected");

        Table table = new Table();
        table.setWidth(50, Unit.PERCENTAGE);
        table.setHeight(50, Unit.PERCENTAGE);
        table.setId("halfwithtiny-table");
        absoluteLayout.addComponent(table);

        Table tableTiny = new Table();
        tableTiny.setWidth(50, Unit.PIXELS);
        tableTiny.setHeight(50, Unit.PIXELS);
        absoluteLayout.addComponent(tableTiny, "right:50;");
        return absoluteLayout;
    }

    /**
     * Creates an {@link AbsoluteLayout} of full size that contains a half-sized
     * {@link Table}.
     *
     * @return the created layout
     */
    private Component createHalfTableOnFull() {
        AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setSizeFull();
        absoluteLayout.setId("halfinfull-layout");
        absoluteLayout.setCaption("half-sized table expected");

        Table table = new Table();
        table.setWidth(50, Unit.PERCENTAGE);
        table.setHeight(50, Unit.PERCENTAGE);
        table.setId("halfinfull-table");
        absoluteLayout.addComponent(table);
        return absoluteLayout;
    }

    /**
     * Creates an {@link AbsoluteLayout} of fixed size that contains a
     * fixed-sized {@link AbsoluteLayout}.
     * 
     * @return the created layout
     */
    private Component createFullOnFixed() {
        AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setWidth(200, Unit.PIXELS);
        absoluteLayout.setHeight(200, Unit.PIXELS);
        absoluteLayout.setId("fullonfixed-outer");
        absoluteLayout.addStyleName("green");
        absoluteLayout.setCaption("yellow area expected");

        AbsoluteLayout absoluteLayout2 = new AbsoluteLayout();
        absoluteLayout2.setSizeFull();
        absoluteLayout2.setId("fullonfixed-inner");
        absoluteLayout2.addStyleName("yellow");

        absoluteLayout.addComponent(absoluteLayout2, "top:50px;left:100px;");
        return absoluteLayout;
    }

    /**
     * Creates an {@link AbsoluteLayout} of full size that contains another
     * full-sized {@link AbsoluteLayout}.
     * 
     * @return the created layout
     */
    private AbsoluteLayout createFullOnFull() {
        AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setSizeFull();
        absoluteLayout.setId("fullonfull-outer");
        absoluteLayout.addStyleName("cyan");
        absoluteLayout.setCaption("area with red border expected");

        AbsoluteLayout absoluteLayout2 = new AbsoluteLayout();
        absoluteLayout2.setSizeFull();
        absoluteLayout2.setId("fullonfull-inner");
        absoluteLayout2.addStyleName("redborder");

        absoluteLayout.addComponent(absoluteLayout2, "top:50px;left:100px;");
        return absoluteLayout;
    }

    @Override
    protected String getTestDescription() {
        return "Full size component in AbsoluteLayout shouldn't get undefined size";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13131;
    }

}
