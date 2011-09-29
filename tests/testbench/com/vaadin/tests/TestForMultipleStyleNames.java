/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

/**
 * TODO: Note you need to add Theme under WebContent/VAADIN/Themes/mytheme in
 * order to see actual visible results on the browser. Currently changes are
 * visible only by inspecting DOM.
 * 
 * @author IT Mill Ltd.
 */
public class TestForMultipleStyleNames extends CustomComponent implements
        ValueChangeListener {

    private final VerticalLayout main = new VerticalLayout();

    private Label l;

    private final TwinColSelect s = new TwinColSelect();

    private ArrayList<String> styleNames2;

    public TestForMultipleStyleNames() {
        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(new Label(
                "TK5 supports multiple stylenames for components."));
        main.addComponent(new Label("Note you need to add Theme under"
                + " WebContent/VAADIN/Themes/mytheme"
                + " in order to see actual visible results"
                + " on the browser. Currently changes are"
                + " visible only by inspecting DOM."));

        styleNames2 = new ArrayList<String>();

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

        final String currentStyle = l.getStyleName();
        final String[] tmp = currentStyle.split(" ");
        final ArrayList<String> curStyles = new ArrayList<String>();
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] != "") {
                curStyles.add(tmp[i]);
            }
        }

        final Collection<?> styles = (Collection<?>) s.getValue();

        for (final Iterator<?> iterator = styles.iterator(); iterator.hasNext();) {
            final String styleName = (String) iterator.next();
            if (curStyles.contains(styleName)) {
                // already added
                curStyles.remove(styleName);
            } else {
                l.addStyleName(styleName);
            }
        }
        for (final Iterator<String> iterator2 = curStyles.iterator(); iterator2
                .hasNext();) {
            final String object = iterator2.next();
            l.removeStyleName(object);
        }
    }

}
