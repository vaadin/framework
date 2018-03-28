package com.vaadin.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.TwinColSelect;

/**
 * TODO: Note you need to add Theme under WebContent/VAADIN/Themes/mytheme in
 * order to see actual visible results on the browser. Currently changes are
 * visible only by inspecting DOM.
 *
 * @author Vaadin Ltd.
 */
public class TestForMultipleStyleNames extends CustomComponent
        implements ValueChangeListener {

    private final VerticalLayout main = new VerticalLayout();

    private Label l;

    private final TwinColSelect s = new TwinColSelect();

    private List<String> styleNames2;

    public TestForMultipleStyleNames() {
        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(
                new Label("TK5 supports multiple stylenames for components."));
        main.addComponent(new Label("Note you need to add Theme under"
                + " WebContent/VAADIN/Themes/mytheme"
                + " in order to see actual visible results"
                + " on the browser. Currently changes are"
                + " visible only by inspecting DOM."));

        styleNames2 = new ArrayList<>();

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

    @SuppressWarnings("unchecked")
    @Override
    public void valueChange(ValueChangeEvent event) {

        final String currentStyle = l.getStyleName();
        final List<String> curStyles = new ArrayList<>();
        for (String tmp : currentStyle.split(" ")) {
            if (tmp != "") {
                curStyles.add(tmp);
            }
        }

        final Collection<String> styles = (Collection<String>) s.getValue();

        for (final String styleName : styles) {
            if (curStyles.contains(styleName)) {
                // already added
                curStyles.remove(styleName);
            } else {
                l.addStyleName(styleName);
            }
        }
        for (final String object : curStyles) {
            l.removeStyleName(object);
        }
    }

}
