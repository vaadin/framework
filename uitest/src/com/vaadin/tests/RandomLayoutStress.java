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

package com.vaadin.tests;

import java.util.Random;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * This example demonstrates layouts. Layouts are populated with sample Vaadin
 * UI components.
 * 
 * @author Vaadin Ltd.
 * 
 */
public class RandomLayoutStress extends com.vaadin.server.LegacyApplication {

    private final Random seededRandom = new Random(1);

    // FIXME increasing these settings brings out interesting client-side issues
    // (DOM errors)
    // TODO increasing values "even more" crashes Hosted Mode, pumping Xmx/Xms
    // helps to some extent
    private static final int componentCountA = 50;
    private static final int componentCountB = 50;
    private static final int componentCountC = 200;
    private static final int componentCountD = 50;

    /**
     * Initialize Application. Demo components are added to main window.
     */
    @Override
    public void init() {
        final LegacyWindow mainWindow = new LegacyWindow("Layout demo");
        setMainWindow(mainWindow);

        // Create horizontal ordered layout
        VerticalLayout panelALayout = new VerticalLayout();
        panelALayout.setMargin(true);
        final Panel panelA = new Panel(
                "Panel containing horizontal ordered layout", panelALayout);
        HorizontalLayout layoutA = new HorizontalLayout();
        // Add 4 random components
        fillLayout(layoutA, componentCountA);
        // Add layout to panel
        panelALayout.addComponent(layoutA);

        // Create vertical ordered layout
        VerticalLayout panelBLayout = new VerticalLayout();
        panelBLayout.setMargin(true);
        final Panel panelB = new Panel(
                "Panel containing vertical ordered layout", panelBLayout);
        VerticalLayout layoutB = new VerticalLayout();
        // Add 4 random components
        fillLayout(layoutB, componentCountB);
        // Add layout to panel
        panelBLayout.addComponent(layoutB);

        // Create grid layout
        final int gridSize = (int) java.lang.Math.sqrt(componentCountC);
        VerticalLayout panelGLayout = new VerticalLayout();
        panelGLayout.setMargin(true);
        final Panel panelG = new Panel("Panel containing grid layout ("
                + gridSize + " x " + gridSize + ")", panelGLayout);
        GridLayout layoutG = new GridLayout(gridSize, gridSize);
        // Add 12 random components
        fillLayout(layoutG, componentCountC);
        // Add layout to panel
        panelGLayout.addComponent(layoutG);

        // Create TabSheet
        final TabSheet tabsheet = new TabSheet();
        tabsheet.setCaption("Tabsheet, above layouts are added to this component");
        layoutA = new HorizontalLayout();
        // Add 4 random components
        fillLayout(layoutA, componentCountA);
        tabsheet.addTab(layoutA, "Horizontal ordered layout", null);
        layoutB = new VerticalLayout();
        // Add 4 random components
        fillLayout(layoutB, componentCountB);
        tabsheet.addTab(layoutB, "Vertical ordered layout", null);
        layoutG = new GridLayout(gridSize, gridSize);
        // Add 12 random components
        fillLayout(layoutG, componentCountC);
        tabsheet.addTab(layoutG, "Grid layout (4 x 2)", null);

        // Create custom layout
        VerticalLayout panelCLayout = new VerticalLayout();
        panelCLayout.setMargin(true);
        final Panel panelC = new Panel("Custom layout with style exampleStyle",
                panelCLayout);
        final CustomLayout layoutC = new CustomLayout("exampleStyle");
        // Add 4 random components
        fillLayout(layoutC, componentCountD);
        // Add layout to panel
        panelCLayout.addComponent(layoutC);

        // Add demo panels (layouts) to main window
        mainWindow.addComponent(panelA);
        mainWindow.addComponent(panelB);
        mainWindow.addComponent(panelG);
        mainWindow.addComponent(tabsheet);
        mainWindow.addComponent(panelC);
    }

    private AbstractComponent getRandomComponent(int caption) {
        AbstractComponent result = null;
        final int randint = seededRandom.nextInt(7);
        switch (randint) {
        case 0:
            // Label
            result = new Label();
            result.setCaption("Label component " + caption);
            break;
        case 1:
            // Button
            result = new Button();
            result.setCaption("Button component " + caption);
            break;
        case 2:
            // TextField
            result = new TextField();
            result.setCaption("TextField component " + caption);
            break;
        case 3:
            // Select
            result = new Select("Select " + caption);
            result.setCaption("Select component " + caption);
            ((Select) result).addItem("First item");
            ((Select) result).addItem("Second item");
            ((Select) result).addItem("Third item");
            break;
        case 4:
            // Link
            result = new Link("", new ExternalResource("http://www.vaadin.com"));
            result.setCaption("Link component " + caption);
            break;
        case 5:
            // Link
            VerticalLayout panelLayout = new VerticalLayout();
            panelLayout.setMargin(true);
            result = new Panel(panelLayout);
            result.setCaption("Panel component " + caption);
            panelLayout
                    .addComponent(new Label(
                            "Panel is a container for other components, by default it draws a frame around it's "
                                    + "extremities and may have a caption to clarify the nature of the contained components' purpose."
                                    + " Panel contains an layout where the actual contained components are added, "
                                    + "this layout may be switched on the fly."));
            ((Panel) result).setWidth("250px");
            break;
        case 6:
            // Datefield
            result = new DateField();
            ((DateField) result).setStyleName("calendar");
            ((DateField) result).setValue(new java.util.Date());
            result.setCaption("Calendar component " + caption);
            break;
        case 7:
            // Datefield
            result = new DateField();
            ((DateField) result).setValue(new java.util.Date());
            result.setCaption("Calendar component " + caption);
            break;
        }

        return result;
    }

    /**
     * Add demo components to given layout
     * 
     * @param layout
     */
    private void fillLayout(Layout layout, int numberOfComponents) {
        for (int i = 0; i < numberOfComponents; i++) {
            layout.addComponent(getRandomComponent(i));
        }
    }

}
