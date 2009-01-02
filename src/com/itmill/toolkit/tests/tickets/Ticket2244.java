package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.FormLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2244 extends Application {

    Form form;

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        GridLayout gl = new GridLayout(3, 3);
        gl.setSpacing(true);
        gl.addComponent(new Label("Before form"));
        gl.newLine();

        form = new Form(gl);
        form.setItemDataSource(new BeanItem(new MyBean()));

        gl.addComponent(new Label("After form"));

        w.addComponent(form);

        w.addComponent(new Button("new item", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                form.setItemDataSource(new BeanItem(new MyBean()));

            }

        }));
        w.addComponent(new Button("new bigger item",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        form
                                .setItemDataSource(new BeanItem(
                                        new MyBiggerBean()));

                    }

                }));
        w.addComponent(new Button("new grid layout",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        form.setLayout(new GridLayout());

                    }

                }));
        w.addComponent(new Button("new form layout",
                new Button.ClickListener() {

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
