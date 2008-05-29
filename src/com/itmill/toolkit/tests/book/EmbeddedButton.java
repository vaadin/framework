/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.book;

import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;

public class EmbeddedButton extends CustomComponent implements
        Button.ClickListener {
    Button thebutton;

    public EmbeddedButton(Resource icon) {
        /* Create a Button without a caption. */
        thebutton = new Button();

        /* Set the icon of the button from a resource. */
        thebutton.setIcon(icon);

        /*
         * Set the style to link; this leaves out the button frame so you just
         * have the image in the link.
         */
        thebutton.setStyle("link");

        /* Listen for ClickEvents. */
        thebutton.addListener(this);

        setCompositionRoot(thebutton);
    }

    /** Handle button click events from the button. */
    public void buttonClick(Button.ClickEvent event) {
        thebutton.setIcon(null);
        thebutton.setCaption("You successfully clicked on the icon");
    }
}
