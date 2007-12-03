package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
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

    // used to show the last entered value in the textfields
    Label selectedValue;

    public SelectExample() {
        OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        OrderedLayout horiz = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        main.addComponent(horiz);
        Panel single = new Panel("Single selects");
        single.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(single);
        Panel multi = new Panel("Multi selects");
        multi.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(multi);
        // "last selected" -label
        selectedValue = new Label();

        AbstractSelect sel = new OptionGroup("OptionGroup");
        initSelect(sel);
        single.addComponent(sel);

        sel = new OptionGroup("OptionGroup");
        sel.setMultiSelect(true);
        initSelect(sel);
        multi.addComponent(sel);

        sel = new ListSelect("ListSelect");
        initSelect(sel);
        single.addComponent(sel);

        sel = new ListSelect("ListSelect");
        sel.setMultiSelect(true);
        initSelect(sel);
        multi.addComponent(sel);

        sel = new NativeSelect("NativeSelect");
        initSelect(sel);
        single.addComponent(sel);

        sel = new ComboBox("ComboBox");
        initSelect(sel);
        single.addComponent(sel);

        sel = new TwinColSelect("TwinColSelect");
        initSelect(sel);
        multi.addComponent(sel);
    }

    private static void initSelect(AbstractSelect sel) {
        for (int i = 1; i <= 5; i++) {
            sel.addItem("Item " + i);
        }
        sel.setValue(null);
    }

}
