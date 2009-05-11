/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.book;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;

public class TheButton extends CustomComponent implements Button.ClickListener {
    Button thebutton;

    public TheButton() {
        /* Create a Button with the given caption. */
        thebutton = new Button("Do not push this button");

        /* Listen for ClickEvents. */
        thebutton.addListener(this);

        setCompositionRoot(thebutton);
    }

    /** Handle button click events from the button. */
    public void buttonClick(Button.ClickEvent event) {
        thebutton.setCaption("Do not push this button again");
    }
}
