/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.magi;

import com.itmill.toolkit.ui.AbstractComponentContainer;
import com.itmill.toolkit.ui.Button;

public class TheButtons2 {
    Button thebutton;
    Button secondbutton;

    /** Creates two buttons in given container. */
    public TheButtons2(AbstractComponentContainer container) {
        thebutton = new Button("Do not push this button");
        thebutton.addListener(Button.ClickEvent.class, this, "theButtonClick");
        container.addComponent(thebutton);

        secondbutton = new Button("I am a button too");
        secondbutton.addListener(Button.ClickEvent.class, this,
                "secondButtonClick");
        container.addComponent(secondbutton);
    }

    public void theButtonClick(Button.ClickEvent event) {
        thebutton.setCaption("Do not push this button again");
    }

    public void secondButtonClick(Button.ClickEvent event) {
        secondbutton.setCaption("I am not a number!");
    }
}
