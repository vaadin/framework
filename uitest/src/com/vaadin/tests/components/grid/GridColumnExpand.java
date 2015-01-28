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
package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Reindeer;

@Theme(Reindeer.THEME_NAME)
public class GridColumnExpand extends AbstractTestUI {
    private Grid grid;
    private Label firstInfo = new Label();
    private Label secondInfo = new Label();
    private Column firstColumn;
    private Column secondColumn;

    @Override
    protected void setup(VaadinRequest request) {
        grid = new Grid(PersonContainer.createWithTestData());
        grid.removeAllColumns();
        grid.addColumn("address.streetAddress");
        grid.addColumn("lastName");
        firstColumn = grid.getColumns().get(0);
        secondColumn = grid.getColumns().get(1);

        updateInfoLabels();
        addComponent(grid);
        addComponent(firstInfo);
        addComponent(secondInfo);
        addButtons();
    }

    private void addButtons() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(createButtons(firstColumn));
        layout.addComponent(createButtons(secondColumn));
        layout.setExpandRatio(layout.getComponent(1), 1);
        addComponent(layout);
    }

    private Component createButtons(Column column) {
        CssLayout layout = new CssLayout();
        layout.addComponent(new Label("Column 1"));

        CssLayout widthLayout = new CssLayout();
        layout.addComponent(widthLayout);
        widthLayout.addComponent(new Label("Width"));
        widthLayout.addComponent(createWidthButton(column, -1));
        widthLayout.addComponent(createWidthButton(column, 50));
        widthLayout.addComponent(createWidthButton(column, 200));

        CssLayout minLayout = new CssLayout();
        layout.addComponent(minLayout);
        minLayout.addComponent(new Label("Min width"));
        minLayout.addComponent(createMinButton(column, -1));
        minLayout.addComponent(createMinButton(column, 50));
        minLayout.addComponent(createMinButton(column, 200));

        CssLayout maxLayout = new CssLayout();
        maxLayout.addComponent(new Label("Max width"));
        maxLayout.addComponent(createMaxButton(column, -1));
        maxLayout.addComponent(createMaxButton(column, 50));
        maxLayout.addComponent(createMaxButton(column, 200));
        layout.addComponent(maxLayout);

        CssLayout expandLayout = new CssLayout();
        expandLayout.addComponent(new Label("Expand ratio"));
        expandLayout.addComponent(createExpandButton(column, -1));
        expandLayout.addComponent(createExpandButton(column, 0));
        expandLayout.addComponent(createExpandButton(column, 1));
        expandLayout.addComponent(createExpandButton(column, 2));
        layout.addComponent(expandLayout);

        return layout;
    }

    private Component createWidthButton(final Column column, final double width) {
        return new Button("" + width, new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (width >= 0) {
                    column.setWidth(width);
                } else {
                    column.setWidthUndefined();
                }
                updateInfoLabels();
            }
        });
    }

    private Component createMinButton(final Column column, final double width) {
        return new Button("" + width, new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                column.setMinimumWidth(width);
                updateInfoLabels();
            }
        });
    }

    private Component createMaxButton(final Column column, final double width) {
        return new Button("" + width, new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                column.setMaximumWidth(width);
                updateInfoLabels();
            }
        });
    }

    private Component createExpandButton(final Column column, final int ratio) {
        return new Button("" + ratio, new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                column.setExpandRatio(ratio);
                updateInfoLabels();
            }
        });
    }

    private void updateInfoLabels() {
        updateLabel(firstInfo, firstColumn);
        updateLabel(secondInfo, secondColumn);
    }

    private void updateLabel(Label label, Column column) {
        int expandRatio = column.getExpandRatio();
        double minimumWidth = Math.round(column.getMinimumWidth() * 100) / 100;
        double maximumWidth = Math.round(column.getMaximumWidth() * 100) / 100;
        double width = Math.round(column.getWidth() * 100) / 100;
        Object propertyId = column.getPropertyId();
        label.setValue(String.format(
                "[%s] Expand ratio: %s - min: %s - max: %s - width: %s",
                propertyId, expandRatio, minimumWidth, maximumWidth, width));
    }
}
