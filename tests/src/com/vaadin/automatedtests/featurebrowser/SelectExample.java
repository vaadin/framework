/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.automatedtests.featurebrowser;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

/**
 * Shows some basic fields for value input; TextField, DateField, Slider...
 * 
 * @author IT Mill Ltd.
 */
public class SelectExample extends CustomComponent {

    // listener that shows a value change notification
    private final Field.ValueChangeListener listener = new Field.ValueChangeListener() {
        public void valueChange(ValueChangeEvent event) {
            getWindow().showNotification("" + event.getProperty().getValue());
        }
    };

    public SelectExample() {
        final VerticalLayout main = new VerticalLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        final HorizontalLayout horiz = new HorizontalLayout();
        horiz.setWidth("100%");
        main.addComponent(horiz);
        final Panel single = new Panel("Single selects");
        single.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(single);
        final Panel multi = new Panel("Multi selects");
        multi.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(multi);

        // radio button group
        AbstractSelect sel = new OptionGroup("OptionGroup");
        sel.setDebugId("RadioButtons");
        initSelect(sel);
        single.addComponent(sel);
        // checkbox group
        sel = new OptionGroup("OptionGroup");
        sel.setDebugId("OptionGroup");
        sel.setMultiSelect(true); // TODO: throws if set after listener - why?
        initSelect(sel);
        multi.addComponent(sel);
        // single-select list
        sel = new ListSelect("ListSelect");
        sel.setDebugId("SingleListSelect");
        ((ListSelect) sel).setColumns(15);
        initSelect(sel);
        single.addComponent(sel);
        // multi-select list
        sel = new ListSelect("ListSelect");
        sel.setDebugId("MultiListSelect");
        ((ListSelect) sel).setColumns(15);
        sel.setMultiSelect(true);
        initSelect(sel);
        multi.addComponent(sel);
        // native-style dropdows
        sel = new NativeSelect("NativeSelect");
        sel.setDebugId("NativeSelect");
        ((NativeSelect) sel).setColumns(15);
        initSelect(sel);
        single.addComponent(sel);
        // combobox
        sel = new ComboBox("ComboBox");
        sel.setDebugId("ComboBox");
        ((ComboBox) sel).setColumns(15);
        initSelect(sel);
        single.addComponent(sel);
        // "twin column" select
        sel = new TwinColSelect("TwinColSelect");
        sel.setDebugId("TwinColSelect");
        ((TwinColSelect) sel).setColumns(15);
        initSelect(sel);
        multi.addComponent(sel);
    }

    /*
     * Initialize select with some values, make immediate and add listener.
     */
    private void initSelect(AbstractSelect sel) {
        for (int i = 1; i <= 5; i++) {
            sel.addItem("Item " + i);
        }
        // select one item
        sel.select("Item 1");

        // make immediate, add listener
        sel.setImmediate(true);
        sel.addListener(listener);
    }

}
