/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.absolutelayout;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class AbsoluteLayoutHideComponent extends AbstractTestUI {

    private AbsoluteLayout mainLayout;
    private VerticalLayout topBar = new VerticalLayout();
    private GridLayout menu;
    private TextField editEmail = new TextField();
    private PasswordField editPassword = new PasswordField();

    @Override
    protected void setup(VaadinRequest request) {
        mainLayout = new AbsoluteLayout();
        mainLayout.setImmediate(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");

        topBar.setHeight("50px");
        topBar.setWidth("100%");

        HorizontalLayout layoutLogin = new HorizontalLayout();
        layoutLogin.setSpacing(true);
        layoutLogin.setHeight("100%");

        Label label_eMail = new Label("e-Mail:");
        layoutLogin.addComponent(label_eMail);
        editEmail.setWidth("200px");
        editEmail.setTabIndex(1);
        layoutLogin.addComponent(editEmail);
        layoutLogin.addComponent(new Label(" "));

        layoutLogin.addComponent(new Label(" "));
        Label label_password = new Label("password:");
        layoutLogin.addComponent(label_password);
        editPassword.setWidth("100px");
        editPassword.setTabIndex(2);
        layoutLogin.addComponent(editPassword);
        layoutLogin.addComponent(new Label(" "));

        // btnLogin
        Button btnLogin = new Button();
        btnLogin.setCaption("Login");
        btnLogin.setImmediate(false);
        btnLogin.setWidth("-1px");
        btnLogin.setHeight("-1px");
        btnLogin.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                login();
            }
        });
        layoutLogin.addComponent(btnLogin);

        for (int index = 0; index < layoutLogin.getComponentCount(); index++) {
            layoutLogin.setComponentAlignment(layoutLogin.getComponent(index),
                    Alignment.MIDDLE_CENTER);
        }

        // =====> THIS CODE generates error
        // WITHOUT THIS CODE works fine

        Embedded e = new Embedded("", new ThemeResource(
                "../runo/icons/64/ok.png"));
        // e.setMimeType("image/jpg");
        e.setWidth("100%");
        e.setHeight("100%");
        mainLayout.addComponent(e);

        // =======

        topBar.addComponent(layoutLogin);
        mainLayout.addComponent(topBar, "left:0px;top:0px;");

        menu = buildMenu();
        menu.setVisible(false);
        mainLayout.addComponent(menu, "left:20px;top:70px;");

        setContent(mainLayout);
    }

    private GridLayout buildMenu() {
        GridLayout gridButtons = new GridLayout(2, 3);

        Button btn1 = new Button("Button one");
        btn1.setImmediate(true);
        btn1.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
            }
        });
        gridButtons.addComponent(btn1, 0, 0);

        Button btn2 = new Button("Button two");
        btn2.setImmediate(true);
        btn2.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
            }
        });
        gridButtons.addComponent(btn2, 0, 1);

        Button btn3 = new Button("Button three");
        btn3.setImmediate(true);
        btn3.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
            }
        });
        gridButtons.addComponent(btn3, 1, 0);

        return gridButtons;
    }

    private void login() {
        menu.setVisible(true);
        topBar.setVisible(false);
    }

    @Override
    protected String getTestDescription() {
        return "Clicking on the button should hide the fields and the button but leave the image";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10155;
    }

}
