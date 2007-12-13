/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;

public class TestForAlignments extends CustomComponent {

    public TestForAlignments() {

        final OrderedLayout main = new OrderedLayout();

        final Button b1 = new Button("Right");
        final Button b2 = new Button("Left");
        final Button b3 = new Button("Bottom");
        final Button b4 = new Button("Top");
        final TextField t1 = new TextField("Right aligned");
        final TextField t2 = new TextField("Bottom aligned");
        final DateField d1 = new DateField("Center aligned");
        final DateField d2 = new DateField("Center aligned");

        final OrderedLayout vert = new OrderedLayout();
        vert.addComponent(b1);
        vert.addComponent(b2);
        vert.addComponent(t1);
        vert.addComponent(d1);
        //vert.getSize().setWidth(500);
        vert.setComponentAlignment(b1, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_TOP);
        vert.setComponentAlignment(b2, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_TOP);
        vert.setComponentAlignment(t1, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_TOP);
        vert.setComponentAlignment(d1,
                OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER,
                OrderedLayout.ALIGNMENT_TOP);
        final OrderedLayout hori = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        hori.addComponent(b3);
        hori.addComponent(b4);
        hori.addComponent(t2);
        hori.addComponent(d2);
        //hori.getSize().setHeight(200);
        hori.setComponentAlignment(b3, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);
        hori.setComponentAlignment(b4, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_TOP);
        hori.setComponentAlignment(t2, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_BOTTOM);
        hori.setComponentAlignment(d2, OrderedLayout.ALIGNMENT_LEFT,
                OrderedLayout.ALIGNMENT_VERTICAL_CENTER);

        main.addComponent(vert);
        main.addComponent(hori);

        setCompositionRoot(main);

    }

}
