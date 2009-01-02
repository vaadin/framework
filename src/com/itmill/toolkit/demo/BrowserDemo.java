/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;

/**
 * Demonstrates the use of Embedded and "suggesting" Select by creating a simple
 * web-browser. Note: does not check for recursion.
 * 
 * @author IT Mill Ltd.
 * @see com.itmill.toolkit.ui.Window
 */
public class BrowserDemo extends com.itmill.toolkit.Application implements
        Select.ValueChangeListener {

    // Default URL to open.
    private static final String DEFAULT_URL = "http://www.itmill.com";

    // The embedded page
    Embedded emb = new Embedded();

    @Override
    public void init() {
        // Create and set main window
        final Window browser = new Window("IT Mill Browser");
        setMainWindow(browser);

        // Use the expand layout to allow one component to use as much
        // space as
        // possible.
        final VerticalLayout exl = new VerticalLayout();
        browser.setLayout(exl);
        exl.setSizeFull();

        // create the address combobox
        final Select select = new Select();
        // allow input
        select.setNewItemsAllowed(true);
        // no empty selection
        select.setNullSelectionAllowed(false);
        select.setWidth("100%");
        // no 'go' -button clicking necessary
        select.setImmediate(true);
        // add some pre-configured URLs
        select.addItem(DEFAULT_URL);
        select.addItem("http://www.google.com");
        select.addItem("http://toolkit.itmill.com/demo");
        // add to layout
        exl.addComponent(select);
        // add listener and select initial URL
        select.addListener(this);
        select.setValue(DEFAULT_URL);

        // configure the embedded and add to layout
        emb.setType(Embedded.TYPE_BROWSER);
        emb.setSizeFull();
        exl.addComponent(emb);
        // make the embedded as large as possible
        exl.setExpandRatio(emb, 1.0F);

    }

    public void valueChange(ValueChangeEvent event) {
        final String url = (String) event.getProperty().getValue();
        if (url != null) {
            // the selected url has changed, let's go there
            emb.setSource(new ExternalResource(url));
        }

    }

}
