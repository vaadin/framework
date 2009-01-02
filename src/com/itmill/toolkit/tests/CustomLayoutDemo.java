/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Component.Event;
import com.itmill.toolkit.ui.Component.Listener;

/**
 * This example demonstrates custom layout. All components created here are
 * placed using custom.html file. Custom layouts may be created with any web
 * designer tool such as Dreamweaver. To place Toolkit components into html
 * page, use divs with location tag as an identifier for Toolkit components, see
 * html page (themes/example/layout/custom.html) and source code below. Body
 * panel contents are changed when menu items are clicked. Contents are HTML
 * pages located at themes/example/layout directory.
 * 
 * @author IT Mill Ltd.
 * @since 4.0.0
 * 
 */
public class CustomLayoutDemo extends com.itmill.toolkit.Application implements
        Listener {

    private CustomLayout mainLayout = null;

    private final Panel bodyPanel = new Panel();

    private final TextField username = new TextField("Username");

    private final TextField loginPwd = new TextField("Password");

    private final Button loginButton = new Button("Login", this, "loginClicked");

    private final Tree menu = new Tree();

    /**
     * Initialize Application. Demo components are added to main window.
     */
    @Override
    public void init() {
        final Window mainWindow = new Window("CustomLayout demo");
        setMainWindow(mainWindow);

        // set the application to use example -theme
        setTheme("example");

        // Create custom layout, themes/example/layout/mainLayout.html
        mainLayout = new CustomLayout("mainLayout");
        // wrap custom layout inside a panel
        final Panel customLayoutPanel = new Panel(
                "Panel containing custom layout (mainLayout.html)");
        customLayoutPanel.addComponent(mainLayout);

        // Login components
        loginPwd.setSecret(true);
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
                Label.CONTENT_XHTML));
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
                + username.getValue() + "</em>", Label.CONTENT_XHTML));
    }

    /**
     * Set body panel caption, remove all existing components and add given
     * custom layout in it.
     * 
     */
    public void setBody(String customLayout) {
        bodyPanel.setCaption(customLayout + ".html");
        bodyPanel.removeAllComponents();
        bodyPanel.addComponent(new CustomLayout(customLayout));
    }

    /**
     * Handle all menu events. Updates body panel contents if menu item is
     * clicked.
     */
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
