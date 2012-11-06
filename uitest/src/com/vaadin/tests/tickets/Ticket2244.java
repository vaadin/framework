package com.vaadin.tests.tickets;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket2244 extends LegacyApplication {

    Form form;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);

        GridLayout gl = new GridLayout(3, 3);
        gl.setSpacing(true);
        gl.addComponent(new Label("Before form"));
        gl.newLine();

        form = new Form(gl);
        form.setItemDataSource(new BeanItem<MyBean>(new MyBean()));

        gl.addComponent(new Label("After form"));

        w.addComponent(form);

        w.addComponent(new Button("new item", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                form.setItemDataSource(new BeanItem<MyBean>(new MyBean()));

            }

        }));
        w.addComponent(new Button("new bigger item",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        form.setItemDataSource(new BeanItem<MyBean>(
                                new MyBiggerBean()));

                    }

                }));
        w.addComponent(new Button("new grid layout",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        form.setLayout(new GridLayout());

                    }

                }));
        w.addComponent(new Button("new form layout",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        form.setLayout(new FormLayout());

                    }

                }));

    }

    public class MyBean {
        String firstname;
        String lastname;
        String password;

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

    public class MyBiggerBean extends MyBean {
        String address;
        String phone;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

    }

}
