package com.itmill.toolkit.tests;

import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.ListSelect;
import com.itmill.toolkit.ui.NativeSelect;
import com.itmill.toolkit.ui.OptionGroup;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * Simple test helper to test Focusable.focus() method.
 * 
 */
public class FocusingComponents extends CustomComponent {
    GridLayout lo = new GridLayout(2, 1);

    public FocusingComponents() {

        setCompositionRoot(lo);
        lo.setSpacing(true);

        Focusable f;

        f = new Button();

        addFocusableTest(f);
        addFocusableTest(new ComboBox());
        addFocusableTest(new TextField());
        addFocusableTest(new DateField());
        addFocusableTest(new NativeSelect());
        addFocusableTest(new ListSelect());
        addFocusableTest(new OptionGroup());
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.setMultiSelect(true);
        addFocusableTest(optionGroup);

    }

    private void addFocusableTest(final Focusable f) {

        f.setCaption(f.getClass().getSimpleName());
        lo.addComponent(f);

        if (f instanceof AbstractSelect) {
            AbstractSelect s = (AbstractSelect) f;
            s.addItem("Foo");
            s.addItem("Bar");
        }

        Button focus = new Button("focus");
        focus.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                f.focus();
            }
        });
        lo.addComponent(focus);

    }

}
