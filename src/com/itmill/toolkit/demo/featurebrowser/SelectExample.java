/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.ListSelect;
import com.itmill.toolkit.ui.NativeSelect;
import com.itmill.toolkit.ui.OptionGroup;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TwinColSelect;

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
        final OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        final OrderedLayout horiz = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        main.addComponent(horiz);
        final Panel single = new Panel("Single selects");
        single.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(single);
        final Panel multi = new Panel("Multi selects");
        multi.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(multi);

        // radio button group
        AbstractSelect sel = new OptionGroup("OptionGroup");
        initSelect(sel);
        single.addComponent(sel);
        // checkbox group
        sel = new OptionGroup("OptionGroup");
        sel.setMultiSelect(true); // TODO: throws if set after listener - why?
        initSelect(sel);
        multi.addComponent(sel);
        // single-select list
        sel = new ListSelect("ListSelect");
        ((ListSelect) sel).setColumns(15);
        initSelect(sel);
        single.addComponent(sel);
        // multi-select list
        sel = new ListSelect("ListSelect");
        ((ListSelect) sel).setColumns(15);
        sel.setMultiSelect(true);
        initSelect(sel);
        multi.addComponent(sel);
        // native-style dropdows
        sel = new NativeSelect("NativeSelect");
        ((NativeSelect) sel).setColumns(15);
        initSelect(sel);
        single.addComponent(sel);
        // combobox
        sel = new ComboBox("ComboBox");
        ((ComboBox) sel).setColumns(15);
        initSelect(sel);
        single.addComponent(sel);
        // "twin column" select
        sel = new TwinColSelect("TwinColSelect");
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
