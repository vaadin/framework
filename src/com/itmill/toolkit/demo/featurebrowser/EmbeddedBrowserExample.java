/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Select;

/**
 * Demonstrates the use of Embedded and "suggesting" Select by creating a simple
 * web-browser. Note: does not check for recursion.
 * 
 * @author IT Mill Ltd.
 * @see com.itmill.toolkit.ui.Window
 */
public class EmbeddedBrowserExample extends ExpandLayout implements
        Select.ValueChangeListener {

    // Default URL to open.
    private static final String DEFAULT_URL = "http://www.itmill.com/index_itmill_toolkit.htm";

    // The embedded page
    Embedded emb = new Embedded();

    public EmbeddedBrowserExample() {
        this(new String[] { DEFAULT_URL,
                "http://www.itmill.com/index_developers.htm",
                "http://toolkit.itmill.com/demo/doc/api/",
                "http://www.itmill.com/manual/index.html" });
    }

    public EmbeddedBrowserExample(String[] urls) {
        getSize().setSizeFull();

        // create the address combobox
        final Select select = new Select();
        // allow input
        select.setNewItemsAllowed(true);
        // no empty selection
        select.setNullSelectionAllowed(false);
        // no 'go' -button clicking necessary
        select.setImmediate(true);
        // add some pre-configured URLs
        for (int i = 0; i < urls.length; i++) {
            select.addItem(urls[i]);
        }
        // add to layout
        addComponent(select);
        // add listener and select initial URL
        select.addListener(this);
        select.setValue(urls[0]);

        // configure the embedded and add to layout
        emb.setType(Embedded.TYPE_BROWSER);
        addComponent(emb);
        // make the embedded as large as possible
        expand(emb);

    }

    public void valueChange(ValueChangeEvent event) {
        final String url = (String) event.getProperty().getValue();
        if (url != null) {
            // the selected url has changed, let's go there
            emb.setSource(new ExternalResource(url));
        }

    }

}
