package com.vaadin.tests.components.menubar;

import com.vaadin.annotations.Widgetset;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class MenuBarChangeFromEventListener extends AbstractTestUIWithLog {
    public final static String MENU_CLICKED = "Menu Selected";
    public final static String MENU_CLICKED_BLUR = "Menu Selected after TF Blur event";

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar mb = new MenuBar();
        mb.setCaption("");

        MenuBar.MenuItem mi = mb.addItem("Item to click", null,
                new MenuBar.Command() {
                    @Override
                    public void menuSelected(MenuBar.MenuItem selectedItem) {
                        log(MENU_CLICKED);
                    }
                });
        mb.setId("menuBar");
        TextField tf = new TextField(
                "2. Focus this TextField and then click the menu");
        tf.setId("textField");
        tf.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent event) {
                if (mb.getDescription().isEmpty()) {
                    mb.setDescription("Some Text here");
                } else {
                    mb.setDescription("");
                }
                log(MENU_CLICKED_BLUR);
            }

        });

        addComponent(mb);
        addComponent(tf);
    }
}
