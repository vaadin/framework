package com.itmill.toolkit.demo;

import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Window;

/**
 * This example demonstrates layouts. Layouts are populated with sample Toolkit
 * UI components.
 * 
 * @author IT Mill Ltd.
 * @since 4.0.0
 * 
 */
public class LayoutDemo extends com.itmill.toolkit.Application {

    /**
     * Initialize Application. Demo components are added to main window.
     */
    public void init() {
        Window mainWindow = new Window("Layout demo");
        setMainWindow(mainWindow);

        //
        // Create horizontal ordered layout
        //
        OrderedLayout layoutA = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        // Add 4 panels
        fillLayout(layoutA, 4);

        //
        // Create vertical ordered layout
        //
        OrderedLayout layoutB = new OrderedLayout(
                OrderedLayout.ORIENTATION_VERTICAL);
        // Add 4 panels
        fillLayout(layoutB, 4);

        //
        // Create grid layout
        //
        GridLayout layoutG = new GridLayout(4, 4);
        // Add 16 panels components
        fillLayout(layoutG, 16);

        //
        // Create grid layout
        //
        GridLayout layoutG2 = new GridLayout(4, 4);
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
        TabSheet tabsheet = new TabSheet();
        tabsheet
                .setCaption("Tabsheet, above layouts are added to this component");
        tabsheet.addTab(layoutA, "Horizontal ordered layout", null);
        tabsheet.addTab(layoutB, "Vertical ordered layout", null);
        tabsheet.addTab(layoutG, "First grid layout", null);
        tabsheet.addTab(layoutG2, "Second grid layout", null);

        //
        // Add demo layouts to main window
        //
        mainWindow.addComponent(new Label(
                "<h3>Horizontal ordered layout</h3>Added four components.",
                Label.CONTENT_XHTML));
        mainWindow.addComponent(layoutA);
        mainWindow.addComponent(new Label(
                "<br /><h3>Vertical ordered layout</h3>Added four components.",
                Label.CONTENT_XHTML));
        mainWindow.addComponent(layoutB);
        mainWindow.addComponent(new Label(
                "<br /><h3>Grid Layout (4 x 4)</h3>Added 16 components.",
                Label.CONTENT_XHTML));
        mainWindow.addComponent(layoutG);
        mainWindow
                .addComponent(new Label("<br /><h3>Grid Layout (4 x 4)</h3>"
                        + "Added four panels and four embedded components "
                        + "diagonally with absolute coordinates.",
                        Label.CONTENT_XHTML));
        mainWindow.addComponent(layoutG2);
        mainWindow.addComponent(new Label(
                "<br /><h3>TabSheet</h3>Added above layouts as tabs.",
                Label.CONTENT_XHTML));
        mainWindow.addComponent(tabsheet);

    }

    private Component getExamplePicture(String caption) {
        ClassResource cr = new ClassResource("features/m-bullet-blue.gif", this);
        Embedded em = new Embedded("Embedded " + caption, cr);
        em.setWidth(170);
        return em;
    }

    private Component getExampleComponent(String caption) {
        Panel panel = new Panel();
        panel.setCaption("Panel component " + caption);
        panel
                .addComponent(new Label(
                        "Panel is a container for other components, by default it draws a frame around it's "
                                + "extremities and may have a caption to clarify the nature of the contained components' purpose."
                                + " Panel contains an layout where the actual contained components are added, "
                                + "this layout may be switched on the fly.",
                        Label.CONTENT_XHTML));
        panel.setWidth(222);
        return panel;
    }

    /**
     * Add multiple demo component to given layout.
     * 
     * @param layout
     *                where components are added
     * @param numberOfComponents
     *                to add
     */
    private void fillLayout(Layout layout, int numberOfComponents) {
        for (int i = 1; i <= numberOfComponents; i++) {
            layout.addComponent(getExampleComponent(Integer.toString(i)));
        }
    }

}
