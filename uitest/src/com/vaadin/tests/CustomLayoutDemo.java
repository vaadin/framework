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

package com.vaadin.tests;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

/**
 * This example demonstrates custom layout. All components created here are
 * placed using custom.html file. Custom layouts may be created with any web
 * designer tool such as Dreamweaver. To place Vaadin components into html page,
 * use divs with location tag as an identifier for Vaadin components, see html
 * page (themes/example/layout/custom.html) and source code below. Body panel
 * contents are changed when menu items are clicked. Contents are HTML pages
 * located at themes/example/layout directory.
 * 
 * @author Vaadin Ltd.
 * @since 4.0.0
 * 
 */
public class CustomLayoutDemo extends com.vaadin.server.LegacyApplication
        implements Listener {

    private CustomLayout mainLayout = null;

    private final Panel bodyPanel = new Panel();

    private final TextField username = new TextField("Username");

    private final PasswordField loginPwd = new PasswordField("Password");

    private final Button loginButton = new Button("Login",
            new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    loginClicked();
                }
            });

    private final Tree menu = new Tree();

    /**
     * Initialize Application. Demo components are added to main window.
     */
    @Override
    public void init() {
        final LegacyWindow mainWindow = new LegacyWindow("CustomLayout demo");
        setMainWindow(mainWindow);

        // set the application to use example -theme
        setTheme("tests-components");

        // Create custom layout, themes/example/layout/mainLayout.html
        mainLayout = new CustomLayout("mainLayout");
        // wrap custom layout inside a panel
        VerticalLayout customLayoutPanelLayout = new VerticalLayout();
        customLayoutPanelLayout.setMargin(true);
        final Panel customLayoutPanel = new Panel(
                "Panel containing custom layout (mainLayout.html)",
                customLayoutPanelLayout);
        customLayoutPanelLayout.addComponent(mainLayout);

        // Login components
        mainLayout.addComponent(username, "loginUser");
        mainLayout.addComponent(loginPwd, "loginPassword");
        mainLayout.addComponent(loginButton, "loginButton");

        // Menu component, when clicked bodyPanel is updated
        menu.addItem("Welcome");
        menu.addItem("Products");
        menu.addItem("Support");
        menu.addItem("News");
        menu.addItem("Developers");
        menu.addItem("Contact");
        // "this" handles all menu events, e.g. node clicked event
        menu.addListener(this);
        // Value changes are immediate
        menu.setImmediate(true);
        menu.setNullSelectionAllowed(false);
        mainLayout.addComponent(menu, "menu");

        // Body component
        mainLayout.addComponent(bodyPanel, "body");

        // Initial body are comes from Welcome.html
        setBody("Welcome");

        // Add heading label and custom layout panel to main window
        mainWindow.addComponent(new Label("<h3>Custom layout demo</h3>",
                ContentMode.HTML));
        mainWindow.addComponent(customLayoutPanel);
    }

    /**
     * Login button clicked. Hide login components and replace username
     * component with "Welcome user Username" message.
     * 
     */
    public void loginClicked() {
        username.setVisible(false);
        loginPwd.setVisible(false);
        if (username.getValue().toString().length() < 1) {
            username.setValue("Anonymous");
        }
        mainLayout.replaceComponent(loginButton, new Label("Welcome user <em>"
                + username.getValue() + "</em>", ContentMode.HTML));
    }

    /**
     * Set body panel caption, remove all existing components and add given
     * custom layout in it.
     * 
     */
    public void setBody(String customLayout) {
        VerticalLayout bodyLayout = new VerticalLayout();
        bodyLayout.setMargin(true);
        bodyLayout.addComponent(new CustomLayout(customLayout));
        bodyPanel.setContent(bodyLayout);
        bodyPanel.setCaption(customLayout + ".html");
    }

    /**
     * Handle all menu events. Updates body panel contents if menu item is
     * clicked.
     */
    @Override
    public void componentEvent(Event event) {
        // Check if event occured at fsTree component
        if (event.getSource() == menu) {
            // Check if event is about changing value
            if (event.getClass() == Field.ValueChangeEvent.class) {
                // Update body area with selected item
                setBody(menu.getValue().toString());
            }
            // here we could check for other type of events for tree
            // component
        }
        // here we could check for other component's events
    }

}
