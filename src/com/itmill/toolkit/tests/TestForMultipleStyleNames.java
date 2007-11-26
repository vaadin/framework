package com.itmill.toolkit.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TwinColSelect;

/**
 * TODO: Note you need to add Theme under WebContent/ITMILL/Themes/mytheme in
 * order to see actual visible results on the browser. Currently changes are
 * visible only by inspecting DOM.
 * 
 * @author IT Mill Ltd.
 */
public class TestForMultipleStyleNames extends CustomComponent implements
        ValueChangeListener {

    private final OrderedLayout main = new OrderedLayout();

    private Label l;

    private final TwinColSelect s = new TwinColSelect();

    private ArrayList styleNames2;

    public TestForMultipleStyleNames() {
        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(new Label(
                "TK5 supports multiple stylenames for components."));
        main.addComponent(new Label("Note you need to add Theme under"
                + " WebContent/ITMILL/Themes/mytheme"
                + " in order to see actual visible results"
                + " on the browser. Currently changes are"
                + " visible only by inspecting DOM."));

        styleNames2 = new ArrayList();

        styleNames2.add("red");
        styleNames2.add("bold");
        styleNames2.add("italic");

        s.setContainerDataSource(new IndexedContainer(styleNames2));
        s.addListener(this);
        s.setImmediate(true);
        main.addComponent(s);

        l = new Label("Test labele");
        main.addComponent(l);

    }

    public void valueChange(ValueChangeEvent event) {

        String currentStyle = l.getStyle();
        String[] tmp = currentStyle.split(" ");
        ArrayList curStyles = new ArrayList();
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] != "") {
                curStyles.add(tmp[i]);
            }
        }

        Collection styles = (Collection) s.getValue();

        for (Iterator iterator = styles.iterator(); iterator.hasNext();) {
            String styleName = (String) iterator.next();
            if (curStyles.contains(styleName)) {
                // already added
                curStyles.remove(styleName);
            } else {
                l.addStyleName(styleName);
            }
        }
        for (Iterator iterator2 = curStyles.iterator(); iterator2.hasNext();) {
            String object = (String) iterator2.next();
            l.removeStyleName(object);
        }
    }

}
