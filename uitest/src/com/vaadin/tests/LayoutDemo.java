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

import com.vaadin.server.ClassResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

/**
 * This example demonstrates layouts. Layouts are populated with sample Vaadin
 * UI components.
 * 
 * @author Vaadin Ltd.
 * @since 4.0.0
 * 
 */
public class LayoutDemo extends com.vaadin.server.LegacyApplication {

    /**
     * Initialize Application. Demo components are added to main window.
     */
    @Override
    public void init() {
        final LegacyWindow mainWindow = new LegacyWindow("Layout demo");
        setMainWindow(mainWindow);

        //
        // Create horizontal ordered layout
        //
        final HorizontalLayout layoutA = new HorizontalLayout();
        // Add 4 panels
        fillLayout(layoutA, 4);

        //
        // Create vertical ordered layout
        //
        final VerticalLayout layoutB = new VerticalLayout();
        // Add 4 panels
        fillLayout(layoutB, 4);

        //
        // Create grid layout
        //
        final GridLayout layoutG = new GridLayout(4, 4);
        // Add 16 panels components
        fillLayout(layoutG, 16);

        //
        // Create grid layout
        //
        final GridLayout layoutG2 = new GridLayout(4, 4);
        // Add 4 panels with absolute coordinates (diagonally)
        layoutG2.addComponent(getExampleComponent("x=0, y=0"), 0, 0);
        layoutG2.addComponent(getExampleComponent("x=1, y=1"), 1, 1);
        layoutG2.addComponent(getExampleComponent("x=2, y=2"), 2, 2);
        layoutG2.addComponent(getExampleComponent("x=3, y=3"), 3, 3);
        // Add 4 pictures with absolute coordinates (diagonally)
        layoutG2.addComponent(getExamplePicture("x=3, y=0"), 3, 0);
        layoutG2.addComponent(getExamplePicture("x=2, y=1"), 2, 1);
        layoutG2.addComponent(getExamplePicture("x=1, y=2"), 1, 2);
        layoutG2.addComponent(getExamplePicture("x=0, y=3"), 0, 3);

        //
        // Create TabSheet
        //
        final TabSheet tabsheet = new TabSheet();
        tabsheet.setCaption("Tabsheet, above layouts are added to this component");
        tabsheet.addTab(layoutA, "Horizontal ordered layout", null);
        tabsheet.addTab(layoutB, "Vertical ordered layout", null);
        tabsheet.addTab(layoutG, "First grid layout", null);
        tabsheet.addTab(layoutG2, "Second grid layout", null);

        //
        // Add demo layouts to main window
        //
        mainWindow.addComponent(new Label(
                "<h3>Horizontal ordered layout</h3>Added four components.",
                ContentMode.HTML));
        mainWindow.addComponent(layoutA);
        mainWindow.addComponent(new Label(
                "<br /><h3>Vertical ordered layout</h3>Added four components.",
                ContentMode.HTML));
        mainWindow.addComponent(layoutB);
        mainWindow.addComponent(new Label(
                "<br /><h3>Grid Layout (4 x 4)</h3>Added 16 components.",
                ContentMode.HTML));
        mainWindow.addComponent(layoutG);
        mainWindow.addComponent(new Label("<br /><h3>Grid Layout (4 x 4)</h3>"
                + "Added four panels and four embedded components "
                + "diagonally with absolute coordinates.", ContentMode.HTML));
        mainWindow.addComponent(layoutG2);
        mainWindow.addComponent(new Label(
                "<br /><h3>TabSheet</h3>Added above layouts as tabs.",
                ContentMode.HTML));
        mainWindow.addComponent(tabsheet);

    }

    private Component getExamplePicture(String caption) {
        // loads image from package com.vaadin.demo
        final ClassResource cr = new ClassResource("m-bullet-blue.gif");
        final Embedded em = new Embedded("Embedded " + caption, cr);
        em.setWidth("170px");
        return em;
    }

    private Component getExampleComponent(String caption) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        final Panel panel = new Panel(layout);
        panel.setCaption("Panel component " + caption);
        layout.addComponent(new Label(
                "Panel is a container for other components, by default it draws a frame around it's "
                        + "extremities and may have a caption to clarify the nature of the contained components' purpose."
                        + " Panel contains an layout where the actual contained components are added, "
                        + "this layout may be switched on the fly.",
                ContentMode.HTML));
        panel.setWidth("222px");
        return panel;
    }

    /**
     * Add multiple demo component to given layout.
     * 
     * @param layout
     *            where components are added
     * @param numberOfComponents
     *            to add
     */
    private void fillLayout(Layout layout, int numberOfComponents) {
        for (int i = 1; i <= numberOfComponents; i++) {
            layout.addComponent(getExampleComponent(Integer.toString(i)));
        }
    }

}
