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
package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.components.grid.GridDragger;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridDraggerTwoGridsBothWays extends AbstractGridDnD {

    @Override
    protected void setup(VaadinRequest request) {
        getUI().setMobileHtml5DndEnabled(true);

        Grid<Person> left = createGridAndFillWithData(25);
        Grid<Person> right = createGridAndFillWithData(25);

        GridDragger<Person> leftToRight = new GridDragger<>(left, right);
        GridDragger<Person> rightToLeft = new GridDragger<>(right, left);

        leftToRight.getGridDragSource()
                .addDragStartListener(event -> rightToLeft.getGridDropTarget()
                        .setDropEffect(DropEffect.NONE));
        leftToRight.getGridDragSource().addDragEndListener(
                event -> rightToLeft.getGridDropTarget().setDropEffect(null));

        rightToLeft.getGridDragSource()
                .addDragStartListener(event -> leftToRight.getGridDropTarget()
                        .setDropEffect(DropEffect.NONE));
        rightToLeft.getGridDragSource().addDragEndListener(
                event -> leftToRight.getGridDropTarget().setDropEffect(null));

        Layout layout = new HorizontalLayout();

        layout.addComponent(left);
        layout.addComponent(right);
        layout.setWidth("100%");
        addComponent(layout);
    }

}
