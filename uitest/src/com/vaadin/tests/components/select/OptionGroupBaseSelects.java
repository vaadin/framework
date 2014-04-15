package com.vaadin.tests.components.select;

import java.util.Iterator;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TwinColSelect;

public class OptionGroupBaseSelects extends ComponentTestCase<HorizontalLayout> {

    private HorizontalLayout layout;

    @Override
    protected Class<HorizontalLayout> getTestClass() {
        return HorizontalLayout.class;
    }

    @Override
    protected void initializeComponents() {

        CheckBox cb = new CheckBox("Switch Selects ReadOnly", false);
        cb.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                for (Iterator<Component> it = layout.getComponentIterator(); it
                        .hasNext();) {
                    Component c = it.next();
                    if (c instanceof AbstractSelect) {
                        c.setReadOnly(!c.isReadOnly());
                    }
                }
            }
        });
        CheckBox cb2 = new CheckBox("Switch Selects Enabled", true);
        cb2.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                for (Iterator<Component> it = layout.getComponentIterator(); it
                        .hasNext();) {
                    Component c = it.next();
                    if (c instanceof AbstractSelect) {
                        boolean enabled = !c.isEnabled();
                        c.setEnabled(enabled);
                        c.setCaption(c.getCaption().replace(
                                (enabled ? "disabled" : "enabled"),
                                (enabled ? "enabled" : "disabled")));
                    }
                }
            }
        });
        HorizontalLayout cbs = new HorizontalLayout();
        cbs.setSpacing(true);
        cbs.addComponent(cb);
        cbs.addComponent(cb2);
        addComponent(cbs);

        layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponent(createSelect(
                new ListSelect("List Select, enabled"), true));
        layout.addComponent(createSelect(
                new ListSelect("List Select, disabled"), false));

        layout.addComponent(createSelect(new NativeSelect(
                "Native Select, enabled"), true));
        layout.addComponent(createSelect(new NativeSelect(
                "Native Select, disabled"), false));

        layout.addComponent(createSelect(new OptionGroup(
                "Option Group, enabled"), true));
        layout.addComponent(createSelect(new OptionGroup(
                "Option Group, disabled"), false));

        layout.addComponent(createSelect(new TwinColSelect(
                "Twin Column Select, enabled"), true));
        layout.addComponent(createSelect(new TwinColSelect(
                "Twin Column Select, disabled"), false));

        addTestComponent(layout);

    }

    private AbstractSelect createSelect(AbstractSelect select, boolean enabled) {
        select.addContainerProperty(CAPTION, String.class, null);
        for (int i = 0; i < 10; i++) {
            select.addItem("" + i).getItemProperty(CAPTION)
                    .setValue("Item " + i);
            if (select instanceof OptionGroup && i % 2 == 1) {
                ((OptionGroup) select).setItemEnabled("" + i, false);
            }
        }
        select.setEnabled(enabled);
        select.setImmediate(true);
        return select;
    }
}
