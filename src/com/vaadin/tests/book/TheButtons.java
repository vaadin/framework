/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.book;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Button;

public class TheButtons implements Button.ClickListener {
    Button thebutton;
    Button secondbutton;

    /** Creates two buttons into given container. */
    public TheButtons(AbstractComponentContainer container) {
        thebutton = new Button("Do not push this button");
        thebutton.addListener(this);
        container.addComponent(thebutton);

        secondbutton = new Button("I am a button too");
        secondbutton.addListener(this);
        container.addComponent(secondbutton);
    }

    /** Handle button click events from the two buttons. */
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == thebutton) {
            thebutton.setCaption("Do not push this button again");
        } else if (event.getButton() == secondbutton) {
            secondbutton.setCaption("I am not a number");
        }
    }
}
