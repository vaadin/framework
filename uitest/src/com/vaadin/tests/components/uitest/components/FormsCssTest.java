package com.vaadin.tests.components.uitest.components;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.VerticalLayout;

public class FormsCssTest extends HorizontalLayout {

    private TestSampler parent;
    private int debugIdCounter = 0;

    public FormsCssTest(TestSampler parent) {
        this.parent = parent;
        setSpacing(true);
        setWidth("100%");

        VerticalLayout vl = new VerticalLayout();
        FieldGroup fg = new BeanFieldGroup<Person>(Person.class);
        fg.setItemDataSource(new BeanItem<Person>(new Person()));
        for (Object propId : fg.getUnboundPropertyIds()) {
            if (!"address".equals(propId)) {
                vl.addComponent(fg.buildAndBind(propId));
            }
        }

        addComponent(vl);

        LoginForm login = new LoginForm();
        login.setId("form" + debugIdCounter++);
        login.setHeight("150px");
        addComponent(login);

    }

    @Override
    public void addComponent(com.vaadin.ui.Component c) {
        parent.registerComponent(c);
        super.addComponent(c);
    }

}
