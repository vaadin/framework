/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import java.util.ArrayList;
import java.util.Iterator;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.Select;

/**
 * 
 * This Component contains some simple test to see that component updates its
 * contents propertly.
 * 
 * @author IT Mill Ltd.
 */
public class TestForGridLayoutChildComponentRendering extends CustomComponent {

    private final GridLayout main = new GridLayout(2, 3);

    public TestForGridLayoutChildComponentRendering() {

        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(new Label("SDFGFHFHGJGFDSDSSSGFDD"));

        final Link l = new Link();
        l.setCaption("Siirry ITMILLIIN");
        l.setResource(new ExternalResource("http://www.itmill.com/"));
        l.setTargetHeight(200);
        l.setTargetWidth(500);
        l.setTargetBorder(Link.TARGET_BORDER_MINIMAL);
        main.addComponent(l);

        final Select se = new Select("Tästä valitaan");
        se.setCaption("Whattaa select");
        se.addItem("valinta1");
        se.addItem("Valinta 2");

        main.addComponent(se, 0, 1, 1, 1);

        Button b = new Button("refresh view", this, "createNewView");
        main.addComponent(b);

        b = new Button("reorder view", this, "randomReorder");
        main.addComponent(b);

        b = new Button("remove randomly one component", this,
                "removeRandomComponent");
        main.addComponent(b);

    }

    public void randomReorder() {
        final Iterator it = main.getComponentIterator();
        final ArrayList components = new ArrayList();
        while (it.hasNext()) {
            components.add(it.next());
        }

        main.removeAllComponents();

        final int size = components.size();
        final int colspanIndex = ((int) (Math.random() * size) / 2) * 2 + 2;

        for (int i = components.size(); i > 0; i--) {
            final int index = (int) (Math.random() * i);
            if (i == colspanIndex) {
                main.addComponent((Component) components.get(index), 0,
                        (size - i) / 2, 1, (size - i) / 2);
            } else {
                main.addComponent((Component) components.get(index));
            }
            components.remove(index);
        }
    }

    public void removeRandomComponent() {
        final Iterator it = main.getComponentIterator();
        final ArrayList components = new ArrayList();
        while (it.hasNext()) {
            components.add(it.next());
        }
        final int size = components.size();
        final int index = (int) (Math.random() * size);
        main.removeComponent((Component) components.get(index));

    }

}
