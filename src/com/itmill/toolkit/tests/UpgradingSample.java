/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

//
// Millstone imports were replaced
//
// import org.millstone.base.Application;
// import org.millstone.base.ui.*;
// import org.millstone.base.data.*;
//
import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;

/**
 * <p>
 * Example application demonstrating simple user login. This example is from
 * MillStone 3.1.1 examples section. Upgrading from 3.1.1 to 4.0.0 was done by
 * updating imports, also setTheme("corporate") call was added to application
 * init method.
 * </p>
 * 
 * @since 3.1.1
 * @author IT Mill Ltd.
 */
public class UpgradingSample extends Application implements
        Property.ValueChangeListener {

    /* Menu for navigating inside the application. */
    private final Tree menu = new Tree();

    /* Contents of the website */
    private final String[][] pages = {
            { "Welcome", "Welcome to our website..." },
            { "Products", "Public product information." },
            { "Contact", "Public contact information." },
            { "CRM", "CRM Database requiring login." },
            { "Intranet", "Internal information database." } };

    /* Application layout */
    private final GridLayout layout = new GridLayout(2, 1);

    /* Initialize the application */
    @Override
    public void init() {

        // Create the main window of the application
        final Window main = new Window("Login example", layout);
        setMainWindow(main);

        // Add menu and loginbox to the application
        final OrderedLayout l = new OrderedLayout();
        layout.addComponent(l, 0, 0);
        l.addComponent(menu);
        l.addComponent(new LoginBox());

        // Setup menu
        menu.setStyleName("menu");
        menu.addListener(this);
        menu.setImmediate(true);
        addToMenu(new String[] { "Welcome", "Products", "Contact" });
    }

    // Overriding usetUser method is a simple way of updating application
    // privileges when the user is changed
    @Override
    public void setUser(Object user) {
        super.setUser(user);
        if (user != null) {
            addToMenu(new String[] { "CRM", "Intranet" });
        }
    }

    public void addToMenu(String[] items) {
        for (int i = 0; i < items.length; i++) {
            menu.addItem(items[i]);
            menu.setChildrenAllowed(items[i], false);
        }
        if (menu.getValue() == null) {
            menu.setValue(items[0]);
        }
    }

    // Handle menu selection and update visible page
    public void valueChange(Property.ValueChangeEvent event) {
        layout.removeComponent(1, 0);
        final String title = (String) menu.getValue();
        for (int i = 0; i < pages.length; i++) {
            if (pages[i][0].equals(title)) {
                final Panel p = new Panel(pages[i][0]);
                p.addComponent(new Label(pages[i][1]));
                p.setStyleName("strong");
                layout.addComponent(p, 1, 0);
            }
        }
    }

    // Simple loginbox component for the application
    public class LoginBox extends CustomComponent implements
            Application.UserChangeListener {

        // The components this loginbox is composed of
        private final TextField loginName = new TextField("Name");

        private final Button loginButton = new Button("Enter", this, "login");

        private final Panel loginPanel = new Panel("Login");

        private final Panel statusPanel = new Panel();

        private final Button logoutButton = new Button("Logout",
                UpgradingSample.this, "close");

        private final Label statusLabel = new Label();

        // Initialize login component
        public LoginBox() {

            // Initialize the component
            loginPanel.addComponent(loginName);
            loginPanel.addComponent(loginButton);
            loginPanel.setStyleName("strong");
            loginName.setColumns(8);
            statusPanel.addComponent(statusLabel);
            statusPanel.addComponent(logoutButton);

            // Set the status of the loginbox and show correct
            // components
            updateStatus();

            // Listen application user change events
            UpgradingSample.this.addListener(this);
        }

        // Login into application
        public void login() {
            final String name = (String) loginName.getValue();
            if (name != null && name.length() > 0) {
                setUser(name);
            }
            loginName.setValue("");
        }

        // Update login status on application user change events
        public void applicationUserChanged(Application.UserChangeEvent event) {
            updateStatus();
        }

        // Update login status of the component by exposing correct
        // components
        private void updateStatus() {
            statusLabel.setValue("User: " + getUser());
            if (getUser() != null) {
                setCompositionRoot(statusPanel);
            } else {
                setCompositionRoot(loginPanel);
            }
        }
    }
}
