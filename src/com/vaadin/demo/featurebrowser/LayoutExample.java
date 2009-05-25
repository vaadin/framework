/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.featurebrowser;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

/**
 * A few examples of layout possibilities.
 * 
 * @author IT Mill Ltd.
 */
@SuppressWarnings("serial")
public class LayoutExample extends CustomComponent {

    public LayoutExample() {

        final VerticalLayout main = new VerticalLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        final GridLayout g = new GridLayout(2, 5);
        g.setWidth("100%");
        main.addComponent(g);

        // panel
        Panel p = new Panel("This is a normal panel");
        Label l = new Label("A normal panel.");
        p.addComponent(l);
        g.addComponent(p);
        // lightpanel
        p = new Panel("This is a light panel");
        p.setStyleName(Panel.STYLE_LIGHT);
        l = new Label("A light-style panel.");
        p.addComponent(l);
        g.addComponent(p);

        TabSheet ts = new TabSheet();
        g.addComponent(ts, 0, 1, 1, 1);

        VerticalLayout ol = new VerticalLayout();
        ol.setMargin(true);
        ol.addComponent(new Label("Component 1"));
        ol.addComponent(new Label("Component 2"));
        ol.addComponent(new Label("Component 3"));
        ts.addTab(ol, "Vertical OrderedLayout", null);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setMargin(true);
        hl.addComponent(new Label("Component 1"));
        hl.addComponent(new Label("Component 2"));
        hl.addComponent(new Label("Component 3"));
        ts.addTab(hl, "Horizontal OrderedLayout", null);

        final GridLayout gl = new GridLayout(3, 3);
        gl.setMargin(true);
        gl.addComponent(new Label("Component 1.1"));
        gl.addComponent(new Label("Component 1.2"));
        gl.addComponent(new Label("Component 1.3"));
        gl.addComponent(new Label("Component 2.2"), 1, 1);
        gl.addComponent(new Label("Component 3.1"), 0, 2);
        gl.addComponent(new Label("Component 3.3"), 2, 2);
        ts.addTab(gl, "GridLayout", null);

        /*- TODO spitpanel removed for now - do we need it here?
        ts = new TabSheet();
        ts.setHeight(150);
        g.addComponent(ts, 0, 2, 1, 2);

        SplitPanel sp = new SplitPanel();
        sp.addComponent(new Label("Component 1"));
        sp.addComponent(new Label("Component 2"));
        ts.addTab(sp, "Vertical SplitPanel", null);

        sp = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        sp.addComponent(new Label("Component 1"));
        sp.addComponent(new Label("Component 2"));
        ts.addTab(sp, "Horizontal SplitPanel", null);
        -*/

    }
}
