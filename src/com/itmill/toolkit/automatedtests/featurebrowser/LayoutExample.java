/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.automatedtests.featurebrowser;

import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TabSheet;

/**
 * A few examples of layout possibilities.
 * 
 * @author IT Mill Ltd.
 */
public class LayoutExample extends CustomComponent {

    public LayoutExample() {

        final OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        final GridLayout g = new GridLayout(2, 5);
        main.addComponent(g);

        // panel
        Panel p = new Panel("This is a normal panel");
        p.setDebugId("NormalPanel");
        Label l = new Label("A normal panel.");
        p.addComponent(l);
        g.addComponent(p);
        // lightpanel
        p = new Panel("This is a light panel");
        p.setDebugId("LightPanel");
        p.setStyleName(Panel.STYLE_LIGHT);
        l = new Label("A light-style panel.");
        p.addComponent(l);
        g.addComponent(p);

        TabSheet ts = new TabSheet();
        g.addComponent(ts, 0, 1, 1, 1);

        OrderedLayout ol = new OrderedLayout();
        ol.setDebugId("VerticalOrderedLayout");
        ol.setMargin(true);
        ol.addComponent(new Label("Component 1"));
        ol.addComponent(new Label("Component 2"));
        ol.addComponent(new Label("Component 3"));
        ts.addTab(ol, "Vertical OrderedLayout", null);

        ol = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
        ol.setDebugId("HorizontalOrderedLayout");
        ol.setMargin(true);
        ol.addComponent(new Label("Component 1"));
        ol.addComponent(new Label("Component 2"));
        ol.addComponent(new Label("Component 3"));
        ts.addTab(ol, "Horizontal OrderedLayout", null);

        final GridLayout gl = new GridLayout(3, 3);
        gl.setDebugId("GridLayout");
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
