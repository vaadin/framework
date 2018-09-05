package com.vaadin.tests.themes.valo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.OptionGroup;

public class CheckBoxes extends VerticalLayout implements View {
    public CheckBoxes() {
        setSpacing(false);

        Label h1 = new Label("Check Boxes");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        addComponent(row);

        CheckBox check = new CheckBox("Checked", true);
        row.addComponent(check);

        check = new CheckBox(
                "Checked, explicit width, so that the caption should wrap",
                true);
        row.addComponent(check);
        check.setWidth("150px");

        check = new CheckBox("Not checked");
        row.addComponent(check);

        check = new CheckBox(null, true);
        check.setDescription("No caption");
        row.addComponent(check);

        check = new CheckBox("Custom color", true);
        check.addStyleName("color1");
        row.addComponent(check);

        TestIcon testIcon = new TestIcon(30);
        check = new CheckBox("Custom color", true);
        check.addStyleName("color2");
        check.setIcon(testIcon.get());
        row.addComponent(check);

        check = new CheckBox("With Icon", true);
        check.setIcon(testIcon.get());
        row.addComponent(check);

        check = new CheckBox();
        check.setIcon(testIcon.get(true));
        row.addComponent(check);

        check = new CheckBox("Small", true);
        check.addStyleName(ValoTheme.CHECKBOX_SMALL);
        row.addComponent(check);

        check = new CheckBox("Large", true);
        check.addStyleName(ValoTheme.CHECKBOX_LARGE);
        row.addComponent(check);

        check = new CheckBox("Disabled", true);
        check.setEnabled(false);
        check.setIcon(testIcon.get());
        row.addComponent(check);

        check = new CheckBox("Readonly", true);
        check.setReadOnly(true);
        check.setIcon(testIcon.get());
        row.addComponent(check);

        h1 = new Label("Option Groups");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        row = new HorizontalLayout();
        row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        addComponent(row);

        OptionGroup options = new OptionGroup("Choose one, explicit width");
        options.setWidth("200px");
        options.addItem("Option One");
        Item two = options.addItem(
                "Option Two, with a longer caption that should wrap when the components width is explicitly set.");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon(two, testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Choose many, explicit width");
        options.setMultiSelect(true);
        options.setWidth("200px");
        options.addItem("Option One");
        two = options.addItem(
                "Option Two, with a longer caption that should wrap when the components width is explicitly set.");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon(two, testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Choose one, small");
        options.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        options.setMultiSelect(false);
        options.addItem("Option One");
        options.addItem("Option Two");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon("Option Two", testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Choose many, small");
        options.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        options.setMultiSelect(true);
        options.addItem("Option One");
        options.addItem("Option Two");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon("Option Two", testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Choose one, large");
        options.addStyleName(ValoTheme.OPTIONGROUP_LARGE);
        options.setMultiSelect(false);
        options.addItem("Option One");
        options.addItem("Option Two");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon("Option Two", testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Choose many, large");
        options.addStyleName(ValoTheme.OPTIONGROUP_LARGE);
        options.setMultiSelect(true);
        options.addItem("Option One");
        options.addItem("Option Two");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon("Option Two", testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Horizontal items");
        options.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        options.addItem("Option One");
        two = options.addItem("Option Two, with a longer caption");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon(two, testIcon.get());
        options.setItemIcon("Option Three", testIcon.get());
        row.addComponent(options);

        options = new OptionGroup("Horizontal items, explicit width");
        options.setMultiSelect(true);
        options.setWidth("500px");
        options.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        options.addItem("Option One");
        two = options.addItem("Option Two, with a longer caption");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon(two, testIcon.get());
        options.setItemIcon("Option Three", testIcon.get());
        row.addComponent(options);

        options = new OptionGroup("Disabled items");
        options.setEnabled(false);
        options.addItem("Option One");
        options.addItem("Option Two");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon("Option Two", testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
