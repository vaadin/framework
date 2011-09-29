package com.vaadin.tests.components.form;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class FormNotGettingSmaller extends TestBase {

    @Override
    protected void setup() {
        Item item = new PropertysetItem();
        item.addItemProperty("name", new ObjectProperty<String>(
                "Charles Anthony"));
        item.addItemProperty("city", new ObjectProperty<String>("London"));
        item.addItemProperty("isTallPerson", new ObjectProperty<Boolean>(
                Boolean.FALSE));

        Label spacer = new Label();
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.setWidth("100%");
        buttons.addComponent(spacer);
        buttons.addComponent(new Button("OK"));
        buttons.addComponent(new Button("Cancel"));
        buttons.setExpandRatio(spacer, 1f);

        Form form = new Form();
        form.setDescription("Ooh. Just a demonstration of things, really. Some long lorem ipsum dolor sit amet.Some very long lorem ipsum dolor sit amet.Some very long lorem ipsum dolor sit amet.Some very long lorem ipsum dolor sit amet.");

        form.setItemDataSource(item);
        form.setFooter(buttons);

        getLayout().addComponent(form);
    }

    @Override
    protected String getDescription() {
        return "When resizing window buttons should stay on "
                + "right edge of the screent. Form should also get narrower.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3365;
    }

}
